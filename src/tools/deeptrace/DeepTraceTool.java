/******************************************************************************

Copyright (c) 2010, Cormac Flanagan (University of California, Santa Cruz)
                    and Stephen Freund (Williams College) 

All rights reserved.  

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimer in the documentation and/or other materials provided
      with the distribution.

 * Neither the names of the University of California, Santa Cruz
      and Williams College nor the names of its contributors may be
      used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 ******************************************************************************/

package tools.deeptrace;

import acme.util.Util;
import acme.util.decorations.Decoration;
import acme.util.decorations.DecorationFactory;
import acme.util.decorations.DefaultValue;
import acme.util.option.CommandLine;
import rr.annotations.Abbrev;
import rr.barrier.BarrierEvent;
import rr.barrier.BarrierListener;
import rr.barrier.BarrierMonitor;
import rr.event.*;
import rr.state.ShadowThread;
import rr.tool.Tool;

/**
 * Print a human readable event stream.
 */

@Abbrev("DT")
final public class DeepTraceTool extends Tool implements BarrierListener<Integer> {
	private static int count = 0;
	private static String prefixes = "@!#$%*";
	private static long start_time = 0;

	private Decoration<ShadowThread,Integer> printDepthDecoration =
			ShadowThread.makeDecoration("PrintDepth",
					DecorationFactory.Type.MULTIPLE,
			new DefaultValue<ShadowThread, Integer>() {
				public Integer get(ShadowThread st) {
					return 0;
				}
			});

	private Decoration<ShadowThread,EventCount> eventCountDecoration =
			ShadowThread.makeDecoration("EventCount",
					DecorationFactory.Type.MULTIPLE,
					new DefaultValue<ShadowThread, EventCount>() {
						public EventCount get(ShadowThread st) {
							return new EventCount(0);
						}
					});

	private String pads[] = new String[1000];

	public DeepTraceTool(String name, Tool next, CommandLine commandLine) {
		super(name, next, commandLine);

		new BarrierMonitor<Integer>(this, new DefaultValue<Object,Integer>() {
			public Integer get(Object k) {
				return 0;
			}
		});


		String prefix = "[Event]" + prefixes.charAt(count++);

		String pad = prefix;
		for (int i = 0; i < pads.length; i++) {
			pads[i] = pad;
		}
		start_time = System.nanoTime();
	}

	@Override
	public String toString() {
		return super.toString() + pads[0];
	}

	public void logf(ShadowThread currentThread, String format, Object... ops) {
		Util.printf("%s%s[%4d][%d]\t%s\n",
				pads[printDepthDecoration.get(currentThread)],
				currentThread,
				eventCountDecoration.get(currentThread).inc(),
				System.nanoTime() - start_time,
				String.format(format, ops));
	}

	public void log(ShadowThread currentThread, String s) {
		this.logf(currentThread, "%s", s);
	}

	@Override
	public void create(NewThreadEvent e) {
		ShadowThread currentThread = e.getThread();
		logf(currentThread, "[Create]\t\t%s started %s.", currentThread, currentThread.getParent() != null ? "by " + currentThread.getParent() : "");
		super.create(e);
	}

	@Override
	public void enter(MethodEvent e) {
		logf(e.getThread(), "[Enter]\t\t\t%s from %s",e, e.getInvokeInfo());
		printDepthDecoration.set(e.getThread(),printDepthDecoration.get(e.getThread())+1);
		super.enter(e);
	}

	@Override
	public void exit(MethodEvent e) {
		super.exit(e);
		printDepthDecoration.set(e.getThread(),printDepthDecoration.get(e.getThread())-1);
		logf(e.getThread(), "[Exit]\t\t\t%s", e);
	}

	@Override
	public void preStart(StartEvent e) {
		logf(e.getThread(), "[PreStart]\t\t%s", e);
		super.preStart(e);
	}

	@Override
	public void postStart(StartEvent se) {
		logf(se.getThread(), "[PostStart]\t\t%s",se);
		super.postStart(se);
	}

	@Override
	public void preInterrupt(InterruptEvent me) {
		logf(me.getThread(), "[PreInterrupt]\t%s", me);
		super.preInterrupt(me);
	}

	@Override
	public void interrupted(InterruptedEvent e) {
		ShadowThread currentThread = ShadowThread.getCurrentShadowThread();
		logf(currentThread, "[Interrupt]\t\t%s", e);
		super.interrupted(e);
	}

	@Override
	public void access(AccessEvent e) {
		logf(e.getThread(), "[Access]\t\t%s  %s  %s", e, e.getOriginalShadow(), e.getAccessInfo().getLoc());
		super.access(e);
	}

	@Override
	public void volatileAccess(VolatileAccessEvent e) {
		logf(e.getThread(), "[volatileAccess]\t%s  %s", e ,e.getOriginalShadow());
		super.volatileAccess(e);
	}

	@Override
	public boolean testAcquire(AcquireEvent ae) {
		logf(ae.getThread(), "[TestAcquire]\t%s", Util.objectToIdentityString(ae.getLock().getLock()));
		return super.testAcquire(ae);
	}

	@Override
	public void acquire(AcquireEvent e) {
		logf(e.getThread(), "[Acquire]\t\t%s", e);
		super.acquire(e);
	}

	@Override
	public boolean testRelease(ReleaseEvent re) {
		logf(re.getThread(), "[TestRelease]\t%s", Util.objectToIdentityString(re.getLock().getLock()));
		return super.testRelease(re);
	}

	@Override
	public void release(ReleaseEvent e) {
		logf(e.getThread(), "[Release]\t\t%s", e);
		super.release(e);
	}

	 
	@Override
	public void preWait(WaitEvent e) {
		logf(e.getThread(), "[PreWait]\t\t%s", e);
		super.preWait(e);
	}

	@Override
	public void postWait(WaitEvent e) { 
		logf(e.getThread(), "[PostWait]\t\t%s", e);
		super.postWait(e);
	}

	@Override
	public void preNotify(NotifyEvent e) { 
		logf(e.getThread(), "[PreNotify]\t\t%s", e);
		super.preNotify(e);
	}

	@Override
	public void postNotify(NotifyEvent e) { 
		logf(e.getThread(), "[PostNotify]\t\t%s", e);
		super.postNotify(e);
	}

	@Override
	public void preSleep(SleepEvent e) { 
		logf(e.getThread(), "[PreSleep]\t\t%s", e);
		super.preSleep(e);
	}

	@Override
	public void postSleep(SleepEvent e) { 
		logf(e.getThread(), "[PostSleep]\t\t%s", e);
		super.postSleep(e);
	}

	@Override
	public void preJoin(JoinEvent e) { 
		logf(e.getThread(), "[PreJoin]\t\t%s", e);
		super.preJoin(e);
	}

	@Override
	public void postJoin(JoinEvent e) { 
		logf(e.getThread(), "[PostJoin]\t\t%s", e);
		super.postJoin(e);
	}

	@Override
	public void classInitialized(ClassInitializedEvent e) {
		logf(e.getThread(), "[ClassInitialized]\t%s", e);
		super.classInitialized(e);
	}

	@Override
	public void classAccessed(ClassAccessedEvent e) {
		logf(e.getThread(), "[ClassAccessed]\t%s", e);
		super.classAccessed(e);
	}

	@Override
	public void stop(ShadowThread td) {
		logf(td, "[Stop]\t\t%s", td);
		super.stop(td);
	}

	public void postDoBarrier(BarrierEvent<Integer> be) {
		logf(be.getThread(), "[PostDoBarrier]\t%s", be);
	}

	public void preDoBarrier(BarrierEvent<Integer> be) {
		logf(be.getThread(), "[PreDoBarrier]\t%s", be);
	}


}

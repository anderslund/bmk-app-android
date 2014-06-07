/*
 * Copyright 2014 Anders Lund
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lunders.client.android.bmk.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {

	private final ThreadPoolExecutor mThreadPool;

	// Sets the amount of time an idle thread waits before terminating
	private static final int KEEP_ALIVE_TIME = 1;

	// Sets the Time Unit to seconds
	private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

	private static final ThreadPool sINSTANCE = new ThreadPool();

	private static final int MAXIMUM_POOL_SIZE = 8;


	private ThreadPool() {

		// Creates a thread pool manager
		mThreadPool = new ThreadPoolExecutor(
			Runtime.getRuntime().availableProcessors(),
			MAXIMUM_POOL_SIZE,
			KEEP_ALIVE_TIME,
			KEEP_ALIVE_TIME_UNIT,
			new LinkedBlockingQueue<Runnable>());
	}

	public static ThreadPool getInstance() {
		return sINSTANCE;
	}

	public void execute(Runnable r) {
		mThreadPool.execute(r);
	}
}

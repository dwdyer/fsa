// ============================================================================
//   The Football Statistics Applet (http://fsa.footballpredictions.net)
//   � Copyright 2000-2008 Daniel W. Dyer
//
//   This program is free software: you can redistribute it and/or modify
//   it under the terms of the GNU General Public License as published by
//   the Free Software Foundation, either version 3 of the License, or
//   (at your option) any later version.
//
//   This program is distributed in the hope that it will be useful,
//   but WITHOUT ANY WARRANTY; without even the implied warranty of
//   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//   GNU General Public License for more details.
//
//   You should have received a copy of the GNU General Public License
//   along with this program.  If not, see <http://www.gnu.org/licenses/>.
// ============================================================================
package net.footballpredictions.footballstats.swing;

import java.util.concurrent.CountDownLatch;
import javax.swing.SwingUtilities;

/**
 * A task that is executed on a background thread and then updates
 * a Swing GUI.  A task may only be executed once.
 * @author Daniel Dyer
 * @param <V> Type of result generated by the task.
 */
abstract class SwingBackgroundTask<V>
{
    // Used to assign thread IDs to make threads easier to identify when debugging.
    private static int instanceCount = 0;

    private final CountDownLatch latch = new CountDownLatch(1);
    private final int id;

    protected SwingBackgroundTask()
    {
        synchronized (SwingBackgroundTask.class)
        {
            this.id = instanceCount;
            ++instanceCount;
        }
    }


    /**
     * Asynchronous call that begins execution of the task
     * and returns immediately.  The {@link #performTask()} will be
     * invoked on a background thread and, when it has completed,
     * {@link #postProcessing(Object)} will be invoked on the Event
     * Dispatch Thread (or, if there is an exception,
     * {@link #onError(Throwable)} will be invoked instead - also on
     * the EDT).
     * @see #performTask()
     * @see #postProcessing(Object)
     * @see #onError(Throwable)
     * @see #waitForCompletion()
     */
    public void execute()
    {
        Runnable task = new Runnable()
        {
            public void run()
            {
                try
                {
                    final V result = performTask();
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            postProcessing(result);
                            latch.countDown();
                        }
                    });
                }
                // If an exception occurs performing the task, we need
                // to handle it.
                catch (final Throwable throwable)
                {
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            onError(throwable);
                            latch.countDown();
                        }
                    });
                }
            }
        };
        new Thread(task, "SwingBackgroundTask-" + id).start();
    }


    /**
     * Waits for the execution of this task to complete.  If the {@link #execute()}
     * method has not yet been invoked, this method will block indefinitely.
     * @throws InterruptedException If the thread executing the task
     * is interrupted.
     */
    public void waitForCompletion() throws InterruptedException
    {
        latch.await();
    }


    /**
     * Performs the processing of the task and returns a result.
     * Implement in sub-classes to provide the task logic.  This method will
     * run on a background thread and not on the Event Dispatch Thread and
     * therefore should not manipulate any Swing components.
     * @return The result of executing this task.
     * @throws Exception The task may throw an exception, in which case
     * the {@link #onError(Throwable)} method will be invoked instead of
     * {@link #postProcessing(Object)}.
     */
    protected abstract V performTask() throws Exception;


    /**
     * This method is invoked, on the Event Dispatch Thread, after the task
     * has been executed.
     * This empty default implementation should be over-ridden in sub-classes
     * in order to provide GUI updates that should occur following successful
     * task completion.
     * @param result The result from the {@link #performTask()} method.
     */
    protected void postProcessing(V result)
    {
        // Over-ride in sub-class.
    }


    /**
     * This method is invoked, on the Event Dispatch Thread, if there is an
     * exception or error executing the {@link #performTask()} method.
     * This empty default implementation may be over-ridden in sub-classes
     * in order to update the GUI with error information.
     * @param throwable The exception or error that was thrown while executing
     * the task.
     */
    protected void onError(Throwable throwable)
    {
        // Over-ride in sub-class.
    }
}

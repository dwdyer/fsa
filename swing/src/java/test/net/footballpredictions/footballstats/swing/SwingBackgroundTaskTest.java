// ============================================================================
//   The Football Statistics Applet (http://fsa.footballpredictions.net)
//   © Copyright 2000-2008 Daniel W. Dyer
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

import javax.swing.SwingUtilities;
import org.testng.Reporter;
import org.testng.annotations.Test;

/**
 * Unit test for {@link SwingBackgroundTask}.  Ensures code is
 * executed on correct threads.
 * @author Daniel Dyer
 */
public class SwingBackgroundTaskTest
{
    private boolean taskExecuted;
    private boolean taskOnEDT;
    private boolean postProcessingExecuted;
    private boolean postProcessingOnEDT;
    private boolean exceptionHandled;

    @Test
    public void testExecutionThreads() throws InterruptedException
    {
        SwingBackgroundTask<Object> testTask = new SwingBackgroundTask<Object>()
        {
            protected Object performTask()
            {
                taskExecuted = true;
                taskOnEDT = SwingUtilities.isEventDispatchThread();
                return null;
            }

            @Override
            protected void postProcessing(Object result)
            {
                super.postProcessing(result);
                postProcessingExecuted = true;
                postProcessingOnEDT = SwingUtilities.isEventDispatchThread();
            }
        };
        testTask.execute();
        testTask.waitForCompletion();
        assert taskExecuted : "Task was not executed.";
        assert postProcessingExecuted : "Post-processing was not executed.";
        assert !taskOnEDT : "Task was executed on EDT.";
        assert postProcessingOnEDT : "Post-processing was not executed on EDT.";
    }


    /**
     * Exceptions in the {@link SwingBackgroundTask#performTask()} method should
     * not be swallowed, they must be passed to the
     * {@link SwingBackgroundTask#onError(Throwable)} method.
     */
    @Test
    public void testExceptionInTask() throws InterruptedException
    {
        SwingBackgroundTask<Object> testTask = new SwingBackgroundTask<Object>()
        {
            protected Object performTask()
            {
                throw new UnsupportedOperationException("Task failed.");
            }


            @Override
            protected void onError(Throwable throwable)
            {
                super.onError(throwable);
                // Make sure we've been passed the right exception.
                if (throwable.getClass().equals(UnsupportedOperationException.class))
                {
                    exceptionHandled = true;
                }
                else
                {
                    Reporter.log("Wrong exception class: " + throwable.getClass());
                }
            }
        };
        testTask.execute();
        testTask.waitForCompletion();
        assert exceptionHandled : "Exception was not handled.";
    }
}

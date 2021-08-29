package cn.vtohru.orm.util;

public class ExceptionUtil {
    public static RuntimeException createRuntimeException(Throwable e) {
        if (e instanceof RuntimeException)
            return (RuntimeException) e;
        if (e instanceof Error)
            throw (Error) e;
        return new RuntimeException(e);
    }

    /**
     * Gets the stacktrace of the given exception as <code>StringBuffer</code>
     *
     * @param exception
     *          the exception from which to retrieve the stacktrace
     * @return the <code>StringBuilder</code> into which the content has been appended
     */
    public static StringBuilder getStackTrace(Throwable exception) {
        return appendStackTrace(exception, new StringBuilder());
    }

    /**
     * Gets the stacktrace of the given exception as <code>StringBuffer</code>
     *
     * @param exception
     *          the exception from which to retrieve the stacktrace
     * @param stopString
     *          the String on which to stop adding new Exception trace
     * @return the <code>StringBuilder</code> into which the content has been appended
     */
    public static StringBuilder getStackTrace(Throwable exception, String stopString) {
        return appendStackTrace(exception, new StringBuilder(), stopString, -1);
    }

    /**
     * Gets the stacktrace of the given exception as <code>StringBuffer</code> The method will write maximum of ineCount
     * lines of the StackTrace
     *
     * @param exception
     *          the exception from which to retrieve the stacktrace
     * @param lineCount
     *          the number of lines to be written
     * @return the <code>StringBuilder</code> into which the content has been appended
     */
    public static StringBuilder getStackTrace(Throwable exception, int lineCount) {
        return appendStackTrace(exception, new StringBuilder(), null, lineCount);
    }

    /**
     * Appends the stacktrace of the given exception into the given <code>StringBuffer</code>
     *
     * @param exception
     *          the exception from which to retrieve the stacktrace
     * @param buffer
     *          the buffer into which to write the stacktrace
     * @param stopString
     *          the String on which to stop adding new Exception trace
     * @param lineCount
     *          if lineCount is > 0, then this number of lines of the stacktrace is put out
     * @return the <code>StringBuilder</code> into which the content has been appended
     */
    public static StringBuilder appendStackTrace(Throwable exception, StringBuilder buffer, String stopString,
                                                 int lineCount) {
        if (exception == null) {
            return buffer;
        }
        StackTraceElement[] stacks = exception.getStackTrace();
        for (int i = 0; i < stacks.length; i++) {
            String line = stacks[i].toString();
            boolean lcReached = lineCount > 0 && lineCount <= i;
            boolean stopReached = stopString != null && !stopString.isEmpty() && line.contains(stopString);
            if (lcReached || stopReached) {
                break;
            }
            buffer.append(line).append("\n");
        }
        return buffer;
    }

    /**
     * Appends the stacktrace of the given exception into the given <code>StringBuffer</code>
     *
     * @param exception
     *          the exception from which to retrieve the stacktrace
     * @param buffer
     *          the buffer into which to write the stacktrace
     * @return the <code>StringBuffer</code> into which the content has been appended
     */
    public static StringBuilder appendStackTrace(Throwable exception, StringBuilder buffer) {
        return appendStackTrace(exception, buffer, null, -1);
    }
}

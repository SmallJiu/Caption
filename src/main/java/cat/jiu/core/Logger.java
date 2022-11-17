package cat.jiu.core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.IllegalFormatException;

/**
 * a other Logger, use for not wan to implementation Log4j2
 * @author small_jiu
 *
 */
public class Logger {
    private static final DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("HH:mm:ss");
    private String tag;
    public Logger() {
    	String name = new Exception().getStackTrace()[1].getClassName();
    	this.tag = name.substring(name.lastIndexOf(".")+1);
    }
    public Logger(String tag) {
        this.tag = tag;
    }

    public void log(Level level, String msg, Object... args) {
        StringBuilder sb = new StringBuilder("[");
        
        StackTraceElement[] stacks = new Exception().getStackTrace();
        StackTraceElement stack=null;
        String name1 = stacks[1].getClassName(); 
        if(name1.equals("cat.jiu.core.Logger")) {
        	stack = stacks[2];
        }else {
        	stack = stacks[1];
        }
        
        String clazzName = stack.getClassName();
        clazzName = clazzName.substring(clazzName.lastIndexOf(".")+1, clazzName.length()) + ":" + stack.getLineNumber();
        
        sb.append(formatTime.format(LocalDateTime.now()))
          .append("] [")
          .append(Thread.currentThread().getName())
          .append("/")
          .append(clazzName)
          .append("/")
          .append(level)
          .append("] [")
          .append(this.tag)
          .append("]: ");
        
        try {
  			sb.append(String.format(msg.replace("{}", "%s"), args));
  		}catch(IllegalFormatException e) {
  			sb.append("Format error: " + e.getLocalizedMessage());
  		}

        System.out.println(sb.toString());
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    public void info(String msg, Object... args) {
        log(Level.INFO, msg, args);
    }
    public void warning(String msg, Object... args) {
        log(Level.WARN, msg, args);
    }
    public void error(String msg, Object... args) {
        log(Level.ERROR, msg, args);
    }
    public void debug(String msg, Object... args) {
        log(Level.DEBUG, msg, args);
    }
    public void fatal(String msg, Object... args) {
        log(Level.FATAL, msg, args);
    }
    public static enum Level {
        INFO, ERROR, WARN, DEBUG, FATAL;
    }
}

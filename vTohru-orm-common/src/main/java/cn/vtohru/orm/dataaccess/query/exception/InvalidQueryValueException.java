package cn.vtohru.orm.dataaccess.query.exception;

/**
 * Thrown when an invalid value is given for a query condition
 * 
 * @author sschmitt
 * 
 */
public class InvalidQueryValueException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public InvalidQueryValueException(String message) {
    super(message);
  }

  public InvalidQueryValueException(Throwable cause) {
    super(cause);
  }

}

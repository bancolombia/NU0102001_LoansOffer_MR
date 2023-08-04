package co.com.bancolombia.libreinversion.model.exception;

import co.com.bancolombia.libreinversion.model.error.ErroLog;
import lombok.Getter;

@Getter
public class LibreInversionException extends Exception {

	private static final long serialVersionUID = -331759160190551955L;
	private final String errorCode;
	private final String detailError;
	private final String serviceName;
	private final String actionName;
	private final String messageId;
	

	/**
	 *
	 * @param message
	 * @param errorCode
	 * @param detailError
	 * @param serviceName
	 * @param actionName
	 * @param messageId
	 */
	public LibreInversionException(String message, String errorCode, String detailError, String serviceName,
                                  String actionName, String messageId) {
		super(message);
		this.errorCode = errorCode;
		this.detailError = detailError;
		this.serviceName = serviceName;
		this.actionName = actionName;
		this.messageId = messageId;
	}

	public LibreInversionException(ErroLog error) {
		super(error.getDetailError());
		this.errorCode = error.getErrorCode();
		this.detailError = error.getDetailError();
		this.serviceName = error.getServiceName();
		this.actionName = error.getActionName();
		this.messageId = error.getMessageId();
	}
}
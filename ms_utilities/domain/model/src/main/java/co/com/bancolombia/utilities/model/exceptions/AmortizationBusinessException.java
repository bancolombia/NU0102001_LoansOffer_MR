package co.com.bancolombia.utilities.model.exceptions;

import co.com.bancolombia.utilities.model.error.ErrorLog;
import lombok.Getter;

@Getter
public class AmortizationBusinessException extends Exception {

	private static final long serialVersionUID = -331759160190551955L;
	private final String errorCode;
	private final String detailError;
	private final String serviceName;
	private final String actionName;
	private final String messageId;

	public AmortizationBusinessException(String message, String errorCode, String detailError, String serviceName,
										 String actionName, String messageId) {
		super(message);
		this.errorCode = errorCode;
		this.detailError = detailError;
		this.serviceName = serviceName;
		this.actionName = actionName;
		this.messageId = messageId;
	}

	public AmortizationBusinessException(ErrorLog error) {
		super(error.getDetailError());
		this.errorCode = error.getErrorCode();
		this.detailError = error.getDetailError();
		this.serviceName = error.getServiceName();
		this.actionName = error.getActionName();
		this.messageId = error.getMessageId();
	}
}
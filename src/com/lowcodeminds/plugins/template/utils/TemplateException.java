package com.lowcodeminds.plugins.template.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.appiancorp.suiteapi.common.exceptions.AppianStorageException;
import com.appiancorp.suiteapi.common.exceptions.InvalidVersionException;
import com.appiancorp.suiteapi.common.exceptions.PrivilegeException;
import com.appiancorp.suiteapi.content.exceptions.InvalidContentException;


public class TemplateException extends Exception {
	
	private static final Log LOG = LogFactory.getLog(TemplateException.class);

	PluginContext context;

	public TemplateException(PluginContext context, Throwable cause) {

		super(cause.getMessage(), cause);
		this.context = context;

		if (cause instanceof InvalidContentException) {
			context.setErrorOccured(true);
			context.setErrorMessage("InvalidContentException " + cause.getMessage());
			LOG.error(cause);

		}
		if (cause instanceof PrivilegeException) {
			context.setErrorOccured(true);
			context.setErrorMessage("PrivilegeException " + cause.getMessage());
			LOG.error(cause);
		}
		if (cause instanceof InvalidVersionException) {
			context.setErrorOccured(true);
			context.setErrorMessage("InvalidVersionException " + cause.getMessage());
			LOG.error(cause);
		}
		if (cause instanceof AppianStorageException) {
			context.setErrorOccured(true);
			context.setErrorMessage("AppianStorageException " + cause.getMessage());
			LOG.error(cause);

		}

	}

	public TemplateException(String message) {
		super(message);
	}

}

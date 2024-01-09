package com.lowcodeminds.plugins.tasks;

import org.apache.log4j.Logger;

import com.appiancorp.suiteapi.process.exceptions.SmartServiceException;
import com.aspose.words.Document;
import com.aspose.words.FindReplaceOptions;
import com.lowcodeminds.plugins.template.utils.PluginContext;
import com.lowcodeminds.plugins.template.utils.TemplateServices;

public class RemoveHeaderText extends TemplateTasks {

	private final String headerFooterText = "LCMHF";
	private static final Logger LOG = Logger.getLogger(RemoveHeaderText.class);

	public RemoveHeaderText(Document doc, PluginContext context) {
		super(doc, context);
		this.doc = doc;
		this.context = context;

	}

	@Override
	public void applyTemplate() throws SmartServiceException {
		try {
			doc.getRange().replace(headerFooterText, " ", new FindReplaceOptions());
		} catch (Exception e) {
			context.setErrorOccured(true);
			context.setErrorMessage("Error when processing RemoveHeaderText Task");
			LOG.error("Exception in RemoveHeaderText processing ", e);
			throw TemplateServices.createException(e, getClass());
		}

	}

}

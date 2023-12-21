package com.lowcodeminds.plugins.tasks;

import org.apache.log4j.Logger;
import org.junit.Assert;

import com.appiancorp.suiteapi.process.exceptions.SmartServiceException;
import com.aspose.words.CellCollection;
import com.aspose.words.RowCollection;
import com.aspose.words.SaveFormat;
import com.aspose.words.TableCollection;
import com.lowcodeminds.plugins.template.utils.PluginContext;
import com.lowcodeminds.plugins.template.utils.TemplateServices;

public class RowTextTask extends TemplateTasks {

	com.aspose.words.Document doc;
	PluginContext context;
	
	private static final Logger LOG = Logger.getLogger(RowTextTask.class);

	public RowTextTask(com.aspose.words.Document doc, PluginContext context) {
		super(doc, context);
		this.doc = doc;
		this.context = context;

	}

	@Override
	public void applyTemplate() throws SmartServiceException {

		String removeRowText = context.getColumnText();
		try {
				if (!empty(removeRowText)) {
		
					TableCollection tables = doc.getFirstSection().getBody().getTables();
					Assert.assertEquals(tables.getCount(), tables.toArray().length);
		
					if (tables.getCount() > 0) {
						for (int i = 0; i < tables.getCount(); i++) {
							RowCollection rows = tables.get(i).getRows();
							for (int j = 0; j < rows.getCount(); j++) {
								CellCollection cells = rows.get(j).getCells();
								for (int k = 0; k < cells.getCount(); k++) {
									String cellText = cells.get(k).toString(SaveFormat.TEXT).trim();
									if (!empty(cellText)) {
										if (cellText.contains(removeRowText)) {
											rows.get(j).remove();
										}
									}
								}
							}
						}
					}
				}
		}catch(Exception e) {
			context.setErrorOccured(true);
			context.setErrorMessage("Error when processing Raw Text Task");
			LOG.error("Exception in RowTextTask processing " ,e);
			throw TemplateServices.createException(e, getClass());
		}

	}

	public static boolean empty(final String s) {
		// Null-safe, short-circuit evaluation.
		return s == null || s.trim().isEmpty();
	}

}

package com.lowcodeminds.plugins.tasks;

import java.io.InputStream;
import java.util.Map;
import com.aspose.words.*;
import org.apache.log4j.Logger;

import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.process.exceptions.SmartServiceException;
import com.aspose.words.BreakType;
import com.aspose.words.Document;
import com.lowcodeminds.plugins.template.utils.PluginContext;
import com.lowcodeminds.plugins.template.utils.TemplateConstants;
import com.lowcodeminds.plugins.template.utils.TemplateServices;

import com.aspose.words.DocumentBuilder;
import com.aspose.words.HeaderFooter;
import com.aspose.words.HeaderFooterType;
import com.aspose.words.ImportFormatMode;
import com.aspose.words.Section;

public class AddEnclouserDocuemnts extends TemplateTasks {

	private static final Logger LOG = Logger.getLogger(AddEnclouserDocuemnts.class);

	public static String embedBodyTagName = "EnclosureTags";

	public  String[] fieldNames;
	public  String[] fieldValues;

	public AddEnclouserDocuemnts(Document doc, PluginContext context, ContentService contentService) {
		super(doc, context, contentService);
		this.doc = doc;
		this.context = context;
		this.contentService = contentService;

	}

	public AddEnclouserDocuemnts(Document doc, PluginContext context) {
		super(doc, context);
		this.doc = doc;
		this.context = context;
		// this.contentService = contentService;

	}

	@Override
	public void applyTemplate() throws SmartServiceException {
		// TODO Auto-generated method stub

		Long[] documents = context.getEncloserDocuments();
		if (documents == null || documents.length == 0) {
			LOG.info("No Enclosure documents attached");
			return;
		}

		Map<String, String[]> map = TemplateServices.extactTags(embedBodyTagName, context);
		if (map.size() == 0) {
			LOG.info("No json tag array found  for " + embedBodyTagName);
			// return;
		}

		fieldNames = map.get(TemplateConstants.FIELDS);
		fieldValues = map.get(TemplateConstants.VALUES);

		for (Long id : documents) {

			LOG.info("Processing Enclosures  " + id);
			try (InputStream in = TemplateServices.getDocumentInputStream(id, contentService);) {

				com.aspose.words.Document encloserDoc = new com.aspose.words.Document(in);
				if (map.size() != 0)
					encloserDoc.getMailMerge().execute(fieldNames, fieldValues);
				DocumentBuilder builder = new DocumentBuilder(doc);
			
				builder.moveToDocumentEnd();
			//	builder.insertBreak(BreakType.PAGE_BREAK);
				ImportFormatOptions importFormatOptions = new ImportFormatOptions(); { 
					   importFormatOptions.setIgnoreHeaderFooter(true);
			 
				}
				encloserDoc.getFirstSection().getHeadersFooters().linkToPrevious(false);
			    doc.appendDocument(encloserDoc, ImportFormatMode.KEEP_SOURCE_FORMATTING);
			
			} catch (Exception e) {

				context.setErrorOccured(true);
				context.setErrorMessage("Error when Enclosing document Task");
				LOG.error("Exception in Enclosing document  ", e);
				throw TemplateServices.createException(e, getClass());

			}

		}

	}

}

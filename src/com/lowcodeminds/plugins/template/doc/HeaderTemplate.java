package com.lowcodeminds.plugins.template.doc;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.process.exceptions.SmartServiceException;
import com.aspose.words.Field;
import com.aspose.words.FieldIncludeText;
import com.aspose.words.FieldType;
import com.lowcodeminds.plugins.template.utils.DocType;
import com.lowcodeminds.plugins.template.utils.PluginContext;
import com.lowcodeminds.plugins.template.utils.TemplateConstants;
import com.lowcodeminds.plugins.template.utils.TemplateServices;

import java.io.File;
import java.io.InputStream;

public class HeaderTemplate extends TemplatePage {

	public static String headerTagName = "HeaderTags";

	public final String tempDocNameValue = "tempDocument";
	public final String tempDocExtensionValue = "doc";

	Long headerCreatedDocument;
	
	private static final Log LOG = LogFactory.getLog(HeaderTemplate.class);

	public HeaderTemplate(ContentService contentService, PluginContext context, com.aspose.words.Document doc,
			ContentService tmpContentService) {
		super(contentService, context, doc, tmpContentService);
	}

	@Override
	public void applyTemplating() throws SmartServiceException {
		
		
		if(context.isErrorOccured()) {
			LOG.info("Header Template will not be processed  due to error");
			return;
		}
		Long[] documents = context.getHeaderDocuments();
		if(documents == null ) {
			LOG.info("No HeaderDocuments is given.Stop processing HeaderDocuments Template processing");
			return;
		}
			
		 Map<String , String[]> map = TemplateServices.extactTags(headerTagName,context);
		 if(map.size()== 0) {
			 LOG.info(" No json tag array found  for "+ headerTagName ); 
			return ;
		 }
		 fieldNames = map.get(TemplateConstants.FIELDS);
		 fieldValues = map.get(TemplateConstants.VALUES);

		try {
			InputStream ins = getIncludeStream(documents);
			/*String path = getIncudeDocument(documents);
			
			if(empty(path)) {
				LOG.info("No FIELD_INCLUDE_TEXT  found for HEADER ");
				return;
			}else
				LOG.info(" FIELD_INCLUDE_TEXT  found for HEADER :" + path);
			*/
			com.aspose.words.Document headerdoc = new com.aspose.words.Document(ins);
			headerdoc.getMailMerge().execute(fieldNames, fieldValues);
			
			headerCreatedDocument = TemplateServices.createDocument(tempDocNameValue, tempDocExtensionValue, DocType.DOC,context,tmpContentService);
		//	String headerFilePath = tmpContentService.getInternalFilename(headerCreatedDocument);
			
			File tempFile = File.createTempFile(tempDocNameValue, tempDocExtensionValue);
			String headerFilePath = tempFile.getAbsolutePath();
			LOG.info("Temp file created at: " + tempFile.getAbsolutePath());

			headerdoc.save(tempFile.getAbsolutePath());
			//headerdoc.save(headerFilePath);
			for (Field field : doc.getRange().getFields()) {
				if (field.getType() == FieldType.FIELD_INCLUDE_TEXT) {
					FieldIncludeText iT = (FieldIncludeText) field;

					if (!empty(headerFilePath) && iT.getSourceFullName().contains(appianDocDisplayName)) {
						iT.setSourceFullName(headerFilePath);
					}
					break;
				}

			}

		} catch (Exception e) {

			context.setErrorOccured(true);
			context.setErrorMessage("Error when processing header template");
			LOG.error("Exception in Header Template processing " ,e);
			throw TemplateServices.createException(e, getClass());
		}

	}


	@Override
	public void cleanUp() {
		try {
			if (headerCreatedDocument != null) {
				contentService.delete(headerCreatedDocument, true);
			}
		}catch(Exception e) {
			LOG.error("Exception in Header Template clean up ");
			context.setErrorOccured(true);
			context.setErrorMessage("Error when clening up header resources");
		}
		
	}

}

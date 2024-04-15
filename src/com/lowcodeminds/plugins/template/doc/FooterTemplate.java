package com.lowcodeminds.plugins.template.doc;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import org.apache.log4j.Logger;

import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.process.exceptions.SmartServiceException;
import com.aspose.words.Bookmark;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.Field;
import com.aspose.words.FieldIncludeText;
import com.aspose.words.FieldType;
import com.aspose.words.ImportFormatMode;
import com.lowcodeminds.plugins.template.utils.PluginContext;
import com.lowcodeminds.plugins.template.utils.TemplateConstants;
import com.lowcodeminds.plugins.template.utils.TemplateServices;

public class FooterTemplate extends TemplatePage{
	
	
	public static String footerTagName = "FooterTags";
    public final String tempDocNameValue = "tempDocument";
	public final String tempDocExtensionValue = "doc";

	private static final Logger LOG = Logger.getLogger(FooterTemplate.class);
	
	
	public FooterTemplate(ContentService contentService, PluginContext context, com.aspose.words.Document doc,
			ContentService tmpContentService) {
		super(contentService, context, doc, tmpContentService);
		
	}

	@Override
	public void applyTemplating() throws SmartServiceException {
		Long[] documents= context.getFooterDocuments();
		if(documents == null ) {
			LOG.debug("No Footer Documents are given.Stop processing Footer Document Template processing");
			return;
		}
		
		 Map<String , String[]> map = TemplateServices.extactTags(footerTagName,context);
		 if(map.size()== 0) {
			LOG.debug(" No json tag array found  for "+ footerTagName ); 
			return ;
		 }
		 
		 fieldNames = map.get(TemplateConstants.FIELDS);
		 fieldValues = map.get(TemplateConstants.VALUES);

			
		try(InputStream ins = getIncludeFileStream(documents);) {
					
			if(ins == null) {
				LOG.info("No FIELD_INCLUDE_TEXT  found for FOOTER ");
				return;
			}
			else
				LOG.debug(" FIELD_INCLUDE_TEXT  found for FOOTER :" );
	
			com.aspose.words.Document footerDoc = new com.aspose.words.Document(ins);
			footerDoc.getMailMerge().execute(fieldNames, fieldValues);
			
			//This is to remove unnecessary headers appending
			//https://forum.aspose.com/t/remove-header-when-appending-documents/281541
			footerDoc.getFirstSection().getHeadersFooters().linkToPrevious(false);
				
			LOG.error("creating temparary file");
			tempFile = File.createTempFile("tmp", ".doc");
	
			if(tempFile == null) {
				LOG.error("Temp file is null");
			}
			String headerFilePath = tempFile.getAbsolutePath();
					
			footerDoc.save(headerFilePath);
			
			
			for (Field field : doc.getRange().getFields()) {
				if (field.getType() == FieldType.FIELD_INCLUDE_TEXT) {
					FieldIncludeText iT = (FieldIncludeText) field;

					if (!empty(headerFilePath) && iT.getSourceFullName().contains(getAppianDocDisplayName())) {
						
						//this line was commented as this will cause to incorrect formatting. Inserted  document
						//Formatting was ignored. So instead of setSourceFullName() we are now  inserting document. 
						
						//iT.setSourceFullName(headerFilePath);
						iT.remove();
					}
				//	break;
				}

			}
			//https://forum.aspose.com/t/text-style-change-after-setting-fieldincludetext-for-main-document/281572/5
			//To keep the source  formatting 
			DocumentBuilder builder = new DocumentBuilder(doc);	
			builder.moveToDocumentEnd();
			builder.insertDocument(footerDoc,ImportFormatMode.KEEP_SOURCE_FORMATTING);
	
		} catch (Exception e) {
			context.setErrorOccured(true);
			context.setErrorMessage("Error when processing footer template");
			LOG.error("Exception in Footer Template processing " ,e);
			throw TemplateServices.createException(e, getClass());

		}

		
		
	}

	@Override
	public void cleanUp() {
		try {
			
			if(tempFile != null) {
				tempFile.delete();
			}
		}catch(Exception e) {
			LOG.error("Exception in Footer Template clean up ",e);
		}
		
	}

}

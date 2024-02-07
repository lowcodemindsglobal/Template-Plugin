package com.lowcodeminds.plugins.tags;

import java.util.Map;

import org.apache.log4j.Logger;

import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.process.exceptions.SmartServiceException;
import com.lowcodeminds.plugins.template.utils.PluginContext;
import com.lowcodeminds.plugins.template.utils.TemplateConstants;
import com.lowcodeminds.plugins.template.utils.TemplateServices;

public abstract class Tag {
	
	com.aspose.words.Document doc;
	PluginContext context;
	String tagName;
	
	private static final Logger LOG = Logger.getLogger(Tag.class);
	
	public static String[] fieldNames;
	public static String[] fieldValues;
	
	Tag(com.aspose.words.Document doc, PluginContext context,String tagName){
		this.doc = doc;
		this.context = context;
		this.tagName = tagName;
	}
	

	public  void  apply() throws SmartServiceException {
		
		if(context.isErrorOccured()) {
			return;
		}
		
		 Map<String , String[]> map = TemplateServices.extactTags(tagName,context);
		 
		 if(map.size()== 0) {
			LOG.debug(" No json tag array found  for "+ tagName ); 
			return ;
		 }
		 
		 fieldNames = map.get(TemplateConstants.FIELDS);
		 fieldValues = map.get(TemplateConstants.VALUES);
		 
		 applyTemplate();
		
	}
	
	public  abstract void  applyTemplate() throws SmartServiceException ;
}

package com.lowcodeminds.plugins.documentutilities;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.appiancorp.suiteapi.common.Name;
import com.appiancorp.suiteapi.content.ContentConstants;
import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.content.DocumentInputStream;
import com.appiancorp.suiteapi.content.DocumentOutputStream;
import com.appiancorp.suiteapi.knowledge.Document;
import com.appiancorp.suiteapi.knowledge.DocumentDataType;
import com.appiancorp.suiteapi.knowledge.FolderDataType;
import com.appiancorp.suiteapi.process.exceptions.SmartServiceException;
import com.appiancorp.suiteapi.process.framework.AppianSmartService;
import com.appiancorp.suiteapi.process.framework.Input;
import com.appiancorp.suiteapi.process.framework.Required;
import com.appiancorp.suiteapi.process.framework.SmartServiceContext;
import com.appiancorp.suiteapi.process.palette.PaletteInfo;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import com.lowcodeminds.plugins.tags.DocTag;
import com.lowcodeminds.plugins.tags.HTMLTag;
import com.lowcodeminds.plugins.tags.Tag;
import com.lowcodeminds.plugins.tasks.RowTextTask;
import com.lowcodeminds.plugins.tasks.TemplateTasks;
import com.lowcodeminds.plugins.template.doc.BodyTemplate;
import com.lowcodeminds.plugins.template.doc.FooterImage;
import com.lowcodeminds.plugins.template.doc.FooterTemplate;
import com.lowcodeminds.plugins.template.doc.HeaderImage;
import com.lowcodeminds.plugins.template.doc.HeaderTemplate;
import com.lowcodeminds.plugins.template.doc.TemplatePage;
import com.lowcodeminds.plugins.template.utils.DocType;
import com.lowcodeminds.plugins.template.utils.PluginContext;
import com.lowcodeminds.plugins.template.utils.TemplateConstants;
import com.lowcodeminds.plugins.template.utils.TemplateServices;

@PaletteInfo(paletteCategory = "Appian Smart Services", palette = "Document Management")
public class TemplateSmartService extends AppianSmartService {

	private Long wordDocument;
	private String jsonFormatTags;
	private Long[] headerDocuments;
	private Long[] footerDocuments;
	private Long[] embedBodyDocuments;
	private Long headerImage;
	private Long footerImage;
	private Long licenseFile;
	private String documentName;
	private String columnText;
	private Boolean isPDFGenerate;
	private Long saveInFolder;

	private boolean errorOccured;
	private String errorMessage;

	private Long newGeneratedDocument;
	private Long newPDFGeneratedDocument;

	private final SmartServiceContext smartServiceCtx;
	private final ContentService contentService;
	private final ContentService tempcontentService;

	private final String extensionValue = "docx";
	private final String extensionPDFValue = "pdf";

	String licenseFileName;
	String wordFileName;
	
	private Boolean isGenerateHeaderImageAllPage;
	private Boolean isGenerateFooterImageAllPage;

	private static final Logger LOG = Logger.getLogger(TemplateSmartService.class);
	private DocumentInputStream inputStream;

	@Input(required = Required.ALWAYS)
	@Name("wordDocument")
	@DocumentDataType
	public void setWordDocument(Long val) {
		this.wordDocument = val;
	}

	@Input(required = Required.ALWAYS)

	@Name("JsonFormatTags")
	public void setJsonFormatTags(String val) {
		this.jsonFormatTags = val;
	}

	@Input(required = Required.OPTIONAL)
	@Name("HeaderDocuments")
	@DocumentDataType
	public void setHeaderDocuments(Long[] val) {
		this.headerDocuments = val;
	}

	@Input(required = Required.OPTIONAL)
	@Name("FooterDocuments")
	@DocumentDataType
	public void setFooterDocuments(Long[] val) {
		this.footerDocuments = val;
	}

	@Input(required = Required.OPTIONAL)
	@Name("EmbedBodyDocuments")
	@DocumentDataType
	public void setEmbedBodyDocuments(Long[] val) {
		this.embedBodyDocuments = val;
	}

	@Input(required = Required.OPTIONAL)
	@Name("headerImage")
	@DocumentDataType
	public void setHeaderImage(Long val) {
		this.headerImage = val;
	}

	@Input(required = Required.OPTIONAL)
	@Name("footerImage")
	@DocumentDataType
	public void setFooterImage(Long val) {
		this.footerImage = val;
	}

	@Input(required = Required.ALWAYS)
	@Name("licenseFile")
	@DocumentDataType
	public void setLicenseFile(Long val) {
		this.licenseFile = val;
	}

	@Input(required = Required.ALWAYS)
	@Name("documentName")
	public void setDocumentName(String val) {
		this.documentName = val;
	}

	@Input(required = Required.OPTIONAL)
	@Name("ColumnText")
	public void setColumnText(String val) {
		this.columnText = val;
	}

	@Input(required = Required.OPTIONAL, defaultValue = "false")
	@Name("IsPDFGenerate")
	public void setIsPDFGenerate(Boolean val) {
		this.isPDFGenerate = val;
	}

	@Input(required = Required.ALWAYS)
	@Name("saveInFolder")
	@FolderDataType
	public void setSaveInFolder(Long val) {
		this.saveInFolder = val;
	}

	@Name("newGeneratedDocument")
	public Long getNewGeneratedDocument() {
		return newGeneratedDocument;
	}

	@Name("newPDFGeneratedDocument")
	public Long getNewPDFGeneratedDocument() {
		return newPDFGeneratedDocument;
	}

	@Name("errorOccured")
	public boolean getErrorOccured() {
		return errorOccured;
	}

	@Name("errorMessage")
	public String getErrorMessage() {
		return errorMessage;
	}
	
	@Input(required = Required.OPTIONAL, defaultValue = "false")
	@Name("IsGenerateHeaderImageAllPage")
	public void setIsGenerateHeaderImageAllPage(Boolean val) {
		this.isGenerateHeaderImageAllPage = val;
	}

	@Input(required = Required.OPTIONAL, defaultValue = "false")
	@Name("IsGenerateFooterImageAllPage")
	public void setIsGenerateFooterImageAllPage(Boolean val) {
		this.isGenerateFooterImageAllPage = val;
	}


	public TemplateSmartService(SmartServiceContext smartServiceCtx, ContentService cs_, ContentService temp_cs) {
		super();
		this.smartServiceCtx = smartServiceCtx;
		this.contentService = cs_;
		this.tempcontentService = temp_cs;
	}

	@Override
	public void run() throws SmartServiceException {
		
		LOG.debug("Started Processing  - >" +Thread.currentThread().getId());
		long startTime = System.currentTimeMillis();
		try {

			PluginContext context = createContext();

			Document inputDocument = contentService.download(wordDocument, ContentConstants.VERSION_CURRENT, false)[0];
			inputStream = inputDocument.getInputStream();

			applyLicense(context);

			com.aspose.words.Document doc = new com.aspose.words.Document(inputStream);

			List<TemplatePage> pages = new ArrayList<TemplatePage>();

			// HeaderTemplate
			pages.add(new HeaderTemplate(contentService, context, doc, tempcontentService));

			// BodyTemplate
			pages.add(new BodyTemplate(contentService, context, doc, tempcontentService));

			// FooterTemplate
			pages.add(new FooterTemplate(contentService, context, doc, tempcontentService));

			pages.add(new HeaderImage(contentService, context, doc, tempcontentService));

			pages.add(new FooterImage(contentService, context, doc, tempcontentService));

			// apply templating
			for (TemplatePage t : pages) {
				t.apply();
			}

			// apply Tags
			List<Tag> tags = new ArrayList<Tag>();
			Tag docTag = new DocTag(doc, context, TemplateConstants.DOC_TAG);
			tags.add(docTag);

			Tag htmlTag = new HTMLTag(doc, context, TemplateConstants.HTML_TAG);
			tags.add(htmlTag);
			for (Tag t : tags) {
				t.apply();
			}
			// task
			List<TemplateTasks> tasks = new ArrayList<TemplateTasks>();
			TemplateTasks rawTask = new RowTextTask(doc, context);
			tasks.add(rawTask);

			for (TemplateTasks t : tasks) {
				t.apply();
			}

			// stop document processing
			if (context.isErrorOccured()) {
				LOG.error("Document Templating failed while processing.Stop processing further ");
				return;
			}

			Long finalDocument = TemplateServices.createDocument(documentName, extensionValue, DocType.DOC, context,
					contentService);

			Document tmpDoc = contentService.download(finalDocument, ContentConstants.VERSION_CURRENT, false)[0];
			DocumentOutputStream out = tmpDoc.getOutputStream();
			try (InputStream inputStream = TemplateServices.getDocumentStream(doc, SaveFormat.DOCX)) {
				IOUtils.copy(inputStream, out);
			}
			//int docSize = doc.getBuiltInDocumentProperties().getBytes();
			LOG.debug("Doc format successfully generated");
			// PDF Conversion
			if (isPDFGenerate == true) {

				Long createdPDFDocument = TemplateServices.createDocument(documentName, extensionPDFValue, DocType.PDF,
						context, contentService);
				Document tmppdfDoc = contentService.download(createdPDFDocument, ContentConstants.VERSION_CURRENT,
						false)[0];
				DocumentOutputStream pdfOut = tmppdfDoc.getOutputStream();
				

				try (InputStream inputStream = TemplateServices.getDocumentStream(doc, SaveFormat.PDF)) {
					IOUtils.copy(inputStream, pdfOut);
				}
				
				LOG.debug("PDF format successfully generated");

			}
			long endTime = System.currentTimeMillis();
          
			for (TemplatePage t : pages) {
				t.cleanUp();
			}
			StringBuilder sb = new StringBuilder();
			sb.append("################################################################" +"\n");
			sb.append("Report Name     : " + context.getDocumentName()+"\n");
			sb.append("Thread ID       : " + Thread.currentThread().getId()+"\n");
			sb.append("Time Taken (ms) : " + (endTime - startTime) + " milliseconds" +"\n");
		//	sb.append("Document Size   : " + docSize +"\n");
			sb.append("################################################################");
			
			LOG.info(sb.toString());

		} catch (Exception e) {
			throw TemplateServices.createException(e, getClass());
		}

	}

	public PluginContext createContext() {

		PluginContext context = new PluginContext();
		context.setColumnText(columnText);
		context.setDocumentName(documentName);
		context.setEmbedBodyDocuments(embedBodyDocuments);
		context.setErrorMessage(errorMessage);
		context.setErrorOccured(errorOccured);
		context.setFooterDocuments(footerDocuments);
		context.setHeaderDocuments(headerDocuments);
		context.setHeaderImage(headerImage);
		context.setIsPDFGenerate(isPDFGenerate);
		context.setJsonFormatTags(jsonFormatTags);
		context.setLicenseFile(licenseFile);
		context.setNewGeneratedDocument(newGeneratedDocument);
		context.setNewPDFGeneratedDocument(newPDFGeneratedDocument);
		context.setSaveInFolder(saveInFolder);
		context.setWordDocument(wordDocument);
		context.setFooterImage(footerImage);
		context.setGenerateFooterImageAllPage(isGenerateFooterImageAllPage);
		context.setGenerateHeaderImageAllPage(isGenerateHeaderImageAllPage);
		return context;

	}

	public void applyLicense(PluginContext context) throws SmartServiceException {

		try {
			Document licenseDoc = contentService.download(licenseFile, ContentConstants.VERSION_CURRENT, false)[0];
			InputStream ins = licenseDoc.getInputStream();
			License license = new License();
			license.setLicense(ins);

		} catch (Exception e) {

			context.setErrorOccured(true);
			context.setErrorMessage("Exception License ERROR Using Aspose : " + e.getMessage());
			LOG.error("Aspose License issue ", e);
			throw TemplateServices.createException(e, getClass());
		}
	}


}

package com.lowcodeminds.plugins.documentutilities;

import org.apache.log4j.Logger;

import com.appiancorp.suiteapi.common.Name;
import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.knowledge.DocumentDataType;
import com.appiancorp.suiteapi.knowledge.FolderDataType;
import com.appiancorp.suiteapi.process.exceptions.SmartServiceException;
import com.appiancorp.suiteapi.process.framework.AppianSmartService;
import com.appiancorp.suiteapi.process.framework.Input;
import com.appiancorp.suiteapi.process.framework.Required;
import com.appiancorp.suiteapi.process.framework.SmartServiceContext;
import com.appiancorp.suiteapi.process.palette.PaletteInfo;

//@PaletteInfo(paletteCategory = "Appian Smart Services", palette = "Document Management")
@Deprecated
@PaletteInfo(paletteCategory = "#Deprecated#", palette = "#Deprecated#")
public class ReadAllFields extends AppianSmartService {

	private static final Logger LOG = Logger.getLogger(ReadAllFields.class);
	
	@SuppressWarnings("unused")
	private final SmartServiceContext smartServiceCtx;
	private Long wordDocument;
	private Long licenseFile;
	private Long[] headerDocuments = null;
	private Long[] footerDocuments = null;
	private Long[] embedBodyDocuments = null;
	private Long headerImage = null;
	private Long footerImage = null;

	private Boolean isPDFGenerate;
	private Boolean isGenerateHeaderImageAllPage;
	private Boolean isGenerateFooterImageAllPage;
	private String documentName;
	private String columnText;
	private Long saveInFolder;
	private Long newGeneratedDocument;
	private Long newPDFGeneratedDocument;
	private final ContentService contentService;
	private final ContentService tempcontentService;
	Long createdDocument;
	Long headerCreatedDocument;
	Long footerCreatedDocument;
	Long embedBodyCreatedDocument;
	Long createdPDFDocument;
	String filePath;
	String pdfFilePath;
	String headerFilePath;
	String footerFilePath;
	String embedBodyFilePath;
	private final String extensionValue = "docx";
	private final String extensionPDFValue = "pdf";
	private final String tempDocExtensionValue = "doc";
	private final String headerFooterText = "LCMHF";
	private boolean headerFooterFlag = false;
	private final String tempDocNameValue = "tempDocument";
	String wordFileName;
	String licenseFileName;
	String headerFileName = null;
	String footerFileName = null;
	String embedBodyFileName = null;
	String headerImageFileName = null;
	String footerImageFileName = null;
	private boolean errorOccured;
	private String errorMessage;
	private String jsonFormatTags;
	public static String[] fieldNames;
	public static String[] fieldValues;
	public static String[] fieldHeaderNames;
	public static String[] fieldHeaderValues;
	public static String headerTagName = "HeaderTags";
	public static String footerTagName = "FooterTags";
	public static String embedBodyTagName = "EmbedBodyTags";

	public static String[] headerFooterPath;
	public static int countVal = 0;
	public static String headerDisplayName;
	public static String footerDisplayName;
	public static String embedBodyDisplayName;
	private String firstFooterText = "";

	public void run() throws SmartServiceException {
		
		errorOccured = true;
		errorMessage = "This version of the Template Plugin deprecated,please use the latest smartservice.";
		return;

     }

	

	public ReadAllFields(SmartServiceContext smartServiceCtx, ContentService cs_, ContentService temp_cs) {
		super();
		this.smartServiceCtx = smartServiceCtx;
		this.contentService = cs_;
		this.tempcontentService = temp_cs;
	}

	

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

}

package com.lowcodeminds.plugins.documentutilities;

import java.io.FileInputStream;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import com.appiancorp.suiteapi.common.Name;
import com.appiancorp.suiteapi.common.exceptions.AppianStorageException;
import com.appiancorp.suiteapi.common.exceptions.InvalidVersionException;
import com.appiancorp.suiteapi.common.exceptions.PrivilegeException;
import com.appiancorp.suiteapi.content.ContentConstants;
import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.content.exceptions.InvalidContentException;
import com.appiancorp.suiteapi.knowledge.Document;
import com.appiancorp.suiteapi.knowledge.DocumentDataType;
import com.appiancorp.suiteapi.knowledge.FolderDataType;
import com.appiancorp.suiteapi.process.exceptions.SmartServiceException;
import com.appiancorp.suiteapi.process.framework.AppianSmartService;
import com.appiancorp.suiteapi.process.framework.Input;
import com.appiancorp.suiteapi.process.framework.MessageContainer;
import com.appiancorp.suiteapi.process.framework.Required;
import com.appiancorp.suiteapi.process.framework.SmartServiceContext;
import com.appiancorp.suiteapi.process.palette.PaletteInfo;
import com.aspose.words.CellCollection;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.Field;
import com.aspose.words.FieldIncludeText;
import com.aspose.words.FieldToc;
import com.aspose.words.FieldType;
import com.aspose.words.FindReplaceOptions;
import com.aspose.words.Font;
import com.aspose.words.HeaderFooter;
import com.aspose.words.HeaderFooterType;
import com.aspose.words.IReplacingCallback;
import com.aspose.words.License;
import com.aspose.words.Node;
import com.aspose.words.NodeCollection;
import com.aspose.words.NodeType;
import com.aspose.words.PageSetup;
import com.aspose.words.ParagraphAlignment;
import com.aspose.words.RelativeHorizontalPosition;
import com.aspose.words.RelativeVerticalPosition;
import com.aspose.words.ReplaceAction;
import com.aspose.words.ReplacingArgs;
import com.aspose.words.RowCollection;
import com.aspose.words.Run;
import com.aspose.words.SaveFormat;
import com.aspose.words.Section;
import com.aspose.words.TableCollection;
import com.aspose.words.WrapType;
import com.appiancorp.suiteapi.content.DocumentInputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@PaletteInfo(paletteCategory = "Appian Smart Services", palette = "Document Management")
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

		try {

			Document inputDocument = contentService.download(wordDocument, ContentConstants.VERSION_CURRENT, false)[0];
			wordFileName = inputDocument.getInternalFilename();

			DocumentInputStream inputDocumentStream = contentService.getDocumentInputStream(wordDocument);

			Document licenseDoc = contentService.download(licenseFile, ContentConstants.VERSION_CURRENT, false)[0];
			licenseFileName = licenseDoc.getInternalFilename();

			DocumentInputStream licenseFileNameStream = contentService.getDocumentInputStream(licenseFile);

			if (headerDocuments != null) {

				headerFileName = GetDocumentName(headerDocuments, wordFileName, headerTagName);

			}
			if (footerDocuments != null) {

				footerFileName = GetDocumentName(footerDocuments, wordFileName, footerTagName);

			}
			if (embedBodyDocuments != null) {

				embedBodyFileName = GetDocumentName(embedBodyDocuments, wordFileName, embedBodyTagName);

			}
			if (headerImage != null) {

				Document headerImg = contentService.download(headerImage, ContentConstants.VERSION_CURRENT, false)[0];
				headerImageFileName = headerImg.getInternalFilename();

			}
			if (footerImage != null) {
				Document footerImg = contentService.download(footerImage, ContentConstants.VERSION_CURRENT, false)[0];
				footerImageFileName = footerImg.getInternalFilename();

			}

		} catch (InvalidContentException e1) {
			errorOccured = true;
			errorMessage = "InvalidContentException";
			LOG.error("InvalidContentException");
		} catch (PrivilegeException e) {
			errorOccured = true;
			errorMessage = "PrivilegeException";
			LOG.error("PrivilegeException");
		} catch (InvalidVersionException e) {
			errorOccured = true;
			errorMessage = "InvalidVersionException";
			LOG.error("InvalidVersionException");
		} catch (AppianStorageException e) {
			errorOccured = true;
			errorMessage = "AppianStorageException";
			LOG.error("AppianStorageException");
		}
		try {
			if (errorOccured == false) {
				documentReadAndTagsReplace(wordFileName, documentName, licenseFileName, jsonFormatTags, headerFileName,
						footerFileName, embedBodyFileName, headerImageFileName, footerImageFileName, columnText);
			}

		} catch (Exception e) {
			errorOccured = true;
			errorMessage = "Exception Exception : " + e;
			LOG.error("Exception Exception : " + e);
		}
	}

	public String GetDocumentName(Long[] document, String wordFileName, String nameTag) {

		String matchFileName = "";
		for (int i = 0; i < document.length; i++) {
			try {

				Document displayName = contentService.download(document[i], ContentConstants.VERSION_CURRENT, false)[0];

				if (checkHeaderFooterDocumentInMom(displayName.getDisplayName(), wordFileName, nameTag)) {

					matchFileName = displayName.getInternalFilename();

					break;

				} else {
					// Do something
				}

			} catch (PrivilegeException e) {
				errorOccured = true;
				errorMessage = "PrivilegeException : " + e;
				LOG.error("PrivilegeException : " + e);
			} catch (InvalidVersionException e) {
				errorOccured = true;
				errorMessage = "InvalidVersionException : " + e;
				LOG.error("InvalidVersionException : " + e);
			} catch (Exception e) {
				errorOccured = true;
				errorMessage = "Exception : " + e;
				LOG.error("Exception : " + e);
			}
		}

		return matchFileName;
	}

	public ReadAllFields(SmartServiceContext smartServiceCtx, ContentService cs_, ContentService temp_cs) {
		super();
		this.smartServiceCtx = smartServiceCtx;
		this.contentService = cs_;
		this.tempcontentService = temp_cs;
	}

	private Long createDocument(String documentName, String extension, ContentService CS_Doc) {
		Document document = null;
		Long generatedDocument = null;
		try {
			document = new Document(saveInFolder, documentName, extension);
			document.setState(ContentConstants.STATE_ACTIVE_PUBLISHED);
			document.setFileSystemId(ContentConstants.ALLOCATE_FSID);
			generatedDocument = CS_Doc.create(document, ContentConstants.UNIQUE_NONE);
			if (extension == extensionValue) {
				newGeneratedDocument = CS_Doc.getVersion(generatedDocument, ContentConstants.VERSION_CURRENT).getId();

			}
			if (extension == extensionPDFValue) {
				newPDFGeneratedDocument = CS_Doc.getVersion(generatedDocument, ContentConstants.VERSION_CURRENT)
						.getId();

			}
		} catch (Exception e) {
			errorOccured = true;
			errorMessage = "createDocument Exception : " + e;
			// LOG.error("createDocument Exception : " + e);
		}

		return generatedDocument;

	}

	public static boolean checkHeaderFooterDocumentInMom(String headerDocumentName, String path, String tag)
			throws Exception {
		boolean existHeaderDocInMom = false;
		com.aspose.words.Document doc = new com.aspose.words.Document(path);

		for (Field field : doc.getRange().getFields()) {
			if (field.getType() == FieldType.FIELD_INCLUDE_TEXT) {
				FieldIncludeText fIT = (FieldIncludeText) field;

				if (fIT.getSourceFullName().contains(headerDocumentName)) {
					if (tag == headerTagName) {
						headerDisplayName = headerDocumentName;
					} else if (tag == footerTagName) {
						footerDisplayName = headerDocumentName;
					} else if (tag == embedBodyTagName) {
						embedBodyDisplayName = headerDocumentName;
					}
					existHeaderDocInMom = true;
				}

			}
		}

		return existHeaderDocInMom;

	}

	public static void removeHeader(com.aspose.words.Document doc) throws Exception {
		for (Section section : doc.getSections()) {
//			HeaderFooter header;
//
//			System.out.println("removeHeader :" + doc);
//			header = section.getHeadersFooters().getByHeaderFooterType(HeaderFooterType.HEADER_PRIMARY);
//			System.out.println("header : " + header);
//			if (header != null)
//				header.remove();

			HeaderFooter header;

			// System.out.println("section :" + section.getCount());
			// System.out.println("doc :" + doc.getPageCount());

			header = section.getHeadersFooters().getByHeaderFooterType(HeaderFooterType.HEADER_PRIMARY);
			if (header != null) {
				// header.remove();
				header.getChildNodes().clear();
			}

		}

	}

	@SuppressWarnings("unchecked")
	public void documentReadAndTagsReplace(String wordDocumentPath, String documentName, String licensePath,
			String jsonTags, String headerFilename, String footerFilename, String embedbodyFilename, String headerImage,
			String footerImage, String removeRowText) throws Exception {
		String tocVal = null;
		try {
			FileInputStream fstream = new FileInputStream(licensePath);
			License license = new License();
			license.setLicense(fstream);
		} catch (Exception e) {
			errorOccured = true;
			errorMessage = "Exception License ERROR Using Aspose : " + e.getMessage();
		}
		com.aspose.words.Document doc = new com.aspose.words.Document(wordDocumentPath);

		if (!empty(headerFilename) && errorOccured == false) {

			generateFilePath(jsonTags, headerFilename, headerTagName);

		}
		if (!empty(footerFilename) && errorOccured == false) {

			generateFilePath(jsonTags, footerFilename, footerTagName);

		}

		if (!empty(embedbodyFilename) && errorOccured == false) {

			generateFilePath(jsonTags, embedbodyFilename, embedBodyTagName);

		}

		if (!empty(headerImage) && !empty(footerImage) && errorOccured == false) {

			// Start Header Footer Flag Check

			Section section = doc.getFirstSection();
			int count = 0;
			for (Node node : (NodeCollection<Node>) section.getChildNodes()) {
				// Every node has the NodeType property.

				if (node.getNodeType() == NodeType.HEADER_FOOTER) {

					// headerFooterFlag = true;
					count++;
					System.out.println("count : " + count);
					HeaderFooter headerFooter = (HeaderFooter) node;
					if (count == 2) {
						System.out.println("headerFooter.getText() : " + headerFooter.getText());
						firstFooterText = headerFooter.getText();

					}
					if (headerFooterText.equals(headerFooter.getText().trim())) {

						headerFooterFlag = true;
						// headerFooter.getText().replaceAll(headerFooterText, "");
						System.out.println("headerFooter.getText() : " + headerFooter.getText());
						System.out.println("count : " + count);
					}
				}
//				switch (node.getNodeType()) {
//
//				case NodeType.HEADER_FOOTER: {
//					// If the node type is HeaderFooter, we can cast the node to the HeaderFooter
//					// class.
//					count++;
//					HeaderFooter headerFooter = (HeaderFooter) node;
//
//					// Write the content of the header footer to the console.
//
//					if (headerFooterText.equals(headerFooter.getText().trim())) {
//
//						headerFooterFlag = true;
//						// headerFooter.getText().replaceAll(headerFooterText, "");
//						System.out.println("headerFooter.getText() : "+headerFooter.getText());
//						System.out.println("count : "+count);
//					}
//
//					//break;
//				}
//				default: {
//					// Default
//				}
//				}
			}
			// End Header Footer Flag Check

			if (headerFooterFlag) {
				DocumentBuilder builder = new DocumentBuilder(doc);
				Section currentSection = builder.getCurrentSection();
				PageSetup pageSetup = currentSection.getPageSetup();
				// pageSetup.setDifferentFirstPageHeaderFooter(true); // Header Part
				currentSection.getPageSetup().setDifferentFirstPageHeaderFooter(true);

				// System.out.println("doc.getPageCount() :"+doc.getPageCount());
				// pageSetup.setHeaderDistance(45);

				builder.moveToHeaderFooter(HeaderFooterType.HEADER_FIRST);
				// builder.insertImage(headerImage, RelativeHorizontalPosition.PAGE, 10,
				// RelativeVerticalPosition.PAGE, 10,650, 50, WrapType.THROUGH);
				builder.insertImage(headerImage, RelativeHorizontalPosition.PAGE, 10, RelativeVerticalPosition.PAGE, 10,
						450, 40, WrapType.THROUGH);

				builder.getParagraphFormat().setAlignment(ParagraphAlignment.RIGHT);
				if (isGenerateHeaderImageAllPage == true) {
					builder.moveToHeaderFooter(HeaderFooterType.HEADER_PRIMARY);
					builder.insertImage(headerImage, RelativeHorizontalPosition.PAGE, 0, RelativeVerticalPosition.PAGE,
							0, 0, 0, WrapType.THROUGH);
					builder.getParagraphFormat().setAlignment(ParagraphAlignment.RIGHT);
				}
//				Remove Header other than first Page

				// removeHeader(doc);

				// Footer Part
				pageSetup.setFooterDistance(63); // Newly Added
				builder.moveToHeaderFooter(HeaderFooterType.FOOTER_FIRST);
				builder.insertImage(footerImage, RelativeHorizontalPosition.PAGE, 50, RelativeVerticalPosition.PAGE,
						772, 500, 50, WrapType.TOP_BOTTOM);
				if (firstFooterText != "") {
					builder.getFont().setSize(8);
					// builder.write("\r\n");
					builder.write(firstFooterText);

				}
				if (isGenerateFooterImageAllPage == true) {
					builder.moveToHeaderFooter(HeaderFooterType.FOOTER_PRIMARY);
					builder.insertImage(footerImage, RelativeHorizontalPosition.PAGE, 50, RelativeVerticalPosition.PAGE,
							780, 500, 50, WrapType.TOP_BOTTOM);
				}
				// removeHeader(doc);

				// Header Footer End
			}
		}
		if (errorOccured == false) {
			String escapedString = StringEscapeUtils.unescapeJava(jsonTags);
			boolean headerFlagChk = false;
			boolean footerFlagChk = false;
			boolean embedBodyFlagChk = false;
			try {

				for (Field field : doc.getRange().getFields()) {
					if (field.getType() == FieldType.FIELD_INCLUDE_TEXT) {
						FieldIncludeText iT = (FieldIncludeText) field;

						if (!empty(headerFilePath) && headerFlagChk == false
								&& iT.getSourceFullName().contains(headerDisplayName)) {
							headerFlagChk = true;
							iT.setSourceFullName(headerFilePath);
						} else if (!empty(footerFilePath) && footerFlagChk == false
								&& iT.getSourceFullName().contains(footerDisplayName)) {
							footerFlagChk = true;
							iT.setSourceFullName(footerFilePath);
						} else if (!empty(embedBodyFilePath) && embedBodyFlagChk == false
								&& iT.getSourceFullName().contains(embedBodyDisplayName)) {
							embedBodyFlagChk = true;
							iT.setSourceFullName(embedBodyFilePath);
						}

					}
					if (field.getType() == FieldType.FIELD_TOC) {

						FieldToc toc = (FieldToc) field;
						// toc.setResult(tocVal);
					}

				}

				final JSONObject docObj = new JSONObject(escapedString);
				final JSONArray wordDocTags = docObj.getJSONArray("DocTags");
				fieldNames = new String[wordDocTags.length()];
				fieldValues = new String[wordDocTags.length()];
				if ((docObj.has("DocTags")) && !docObj.getJSONArray("DocTags").isEmpty()) {
					for (int i = 0; i < wordDocTags.length(); ++i) {

						JSONObject wordDocTag = wordDocTags.getJSONObject(i);
						fieldNames[i] = wordDocTag.getString("field");
						fieldValues[i] = wordDocTag.getString("value");

					}

				}

				doc.getMailMerge().execute(fieldNames, fieldValues);

				// HTMLorXML Tags Check

				final JSONObject tagsCheck = new JSONObject(escapedString);

				if ((tagsCheck.has("HtmlTags")) && !tagsCheck.getJSONArray("HtmlTags").isEmpty()) {
					final JSONArray htmlXMLTags = tagsCheck.getJSONArray("HtmlTags");
					for (int x = 0; x < htmlXMLTags.length(); ++x) {
						JSONObject tagsHXCheck = htmlXMLTags.getJSONObject(x);

						if (tagsHXCheck.getString("value").contains("font")) {
							// System.out.println("tagsHXCheck.getString(\"value\") : " +
							// tagsHXCheck.getString("value"));
							FindReplaceOptions findReplaceOptions = new FindReplaceOptions();
							{
								findReplaceOptions.setReplacingCallback(new FindAndInsertHtml());
							}
							doc.getRange().replace(tagsHXCheck.getString("field"), tagsHXCheck.getString("value"),
									findReplaceOptions);
						} else {
							String fontName = null;
							Double fontSize = null;
							// Traverse through the document to find and replace all occurrences
							@SuppressWarnings("unchecked")
							NodeCollection<Run> runs = doc.getChildNodes(NodeType.RUN, true);
							for (Run run : runs) {
								String runText = run.getText();
								// System.out.println("runText : " + runText);
								// System.out.println("tagsHXCheck.getString(\"field\") : " +
								// tagsHXCheck.getString("field"));
								// System.out.println("runText.contains(tagsHXCheck.getString(\"field\")) : "+
								// runText.contains(tagsHXCheck.getString("field")));
								FindReplaceOptions findReplaceOptions = new FindReplaceOptions();
								// System.out.println("runText : " + runText.equals(headerFooterText));
								// System.out.println("runText : " + runText.contains(headerFooterText));

								if (runText.contains(headerFooterText)) {
									System.out.println("runText : " + runText);
									doc.getRange().replace(headerFooterText, "", findReplaceOptions);
								}

								while (runText.contains(tagsHXCheck.getString("field"))) {
									// System.out.println("while true : " + run.getFont());
									Font font = run.getFont();
									fontName = font.getName();
									fontSize = font.getSize();

									// New

									org.jsoup.nodes.Document document = Jsoup.parse(tagsHXCheck.getString("value"));
									Elements body = document.getElementsByTag("body");
									fontSize = fontSize / 0.75;
									String fontStructure = "font-family:" + fontName + ";" + "font-size:" + fontSize
											+ "px;";

									for (Element bd : body) {

										body.attr("style", fontStructure);

									}

									// FindReplaceOptions findReplaceOptions = new FindReplaceOptions();
									{
										findReplaceOptions.setReplacingCallback(new FindAndInsertHtml());
									}
									doc.getRange().replace(tagsHXCheck.getString("field"), document.html(),
											findReplaceOptions);

									break;
								}

							}

//						    org.jsoup.nodes.Document document = Jsoup.parse(tagsHXCheck.getString("value"));
//						    Elements body = document.getElementsByTag("body");
//						    fontSize = fontSize / 0.75;
//							String fontStructure="font-family:"+fontName+";"+"font-size:"+fontSize+ "px;";
//
//						      for (Element bd : body) {
//						            
//						    	  body.attr("style", fontStructure);
//						            
//						           
//						      }
//						   
//						      FindReplaceOptions findReplaceOptions = new FindReplaceOptions();
//								{
//									findReplaceOptions.setReplacingCallback(new FindAndInsertHtml());
//								}
//								doc.getRange().replace(tagsHXCheck.getString("field"), document.html(),
//										findReplaceOptions);   
						}

					}

				}

			} catch (Exception e) {
				errorOccured = true;
				errorMessage = "Exception ERROR Using Aspose : " + e;

			} finally {

//				for (Field field : doc.getRange().getFields()) {
//
//					if (field.getType() == FieldType.FIELD_TOC) {
//
//						FieldToc toc = (FieldToc) field;
//
//						toc.setResult(tocVal);
//
//					}
//				}

			}
		}

		if (!empty(removeRowText) && errorOccured == false) {

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

		if (errorOccured == false) {
			createdDocument = createDocument(documentName, extensionValue, contentService);

			filePath = contentService.getInternalFilename(createdDocument);

			doc.save(filePath);

			contentService.setSizeOfDocumentVersion(createdDocument);

			// PDF Conversion
			if (isPDFGenerate == true) {
				createdPDFDocument = createDocument(documentName, extensionPDFValue, contentService);

				pdfFilePath = contentService.getInternalFilename(createdPDFDocument);

				doc.save(pdfFilePath);

				contentService.setSizeOfDocumentVersion(createdPDFDocument);

			}

			// if (!headerCreatedDocument.toString().trim().isEmpty()) {
			if (headerCreatedDocument != null) {
				contentService.delete(headerCreatedDocument, true);
			}

			if (embedBodyCreatedDocument != null) {
				contentService.delete(embedBodyCreatedDocument, true);
			}

			if (footerCreatedDocument != null) {
				contentService.delete(footerCreatedDocument, true);
			}

		}

	}

	public String htmlTagRemoveRegx(String htmlTag) throws Exception {
		return htmlTag.replaceAll("\\<.*?\\>", "");

	}

	public void generateFilePath(String jsonTags, String fileName, String tagName)
			throws Exception, InvalidContentException {

		com.aspose.words.Document headerdoc = new com.aspose.words.Document(fileName);
		String escapedHeaderTagsString = StringEscapeUtils.unescapeJava(jsonTags);

		final JSONObject obj = new JSONObject(escapedHeaderTagsString);
		final JSONArray headerTags = obj.getJSONArray(tagName);
		fieldHeaderNames = new String[headerTags.length()];
		fieldHeaderValues = new String[headerTags.length()];
		if ((obj.has(tagName)) && !obj.getJSONArray(tagName).isEmpty()) {

			for (int i = 0; i < headerTags.length(); ++i) {

				JSONObject obj1 = headerTags.getJSONObject(i);
				fieldHeaderNames[i] = obj1.getString("field");
				fieldHeaderValues[i] = obj1.getString("value");

			}

		}

		headerdoc.getMailMerge().execute(fieldHeaderNames, fieldHeaderValues);
		if (tagName == headerTagName) {

			headerCreatedDocument = createDocument(tempDocNameValue, tempDocExtensionValue, tempcontentService);
			headerFilePath = tempcontentService.getInternalFilename(headerCreatedDocument);
			headerdoc.save(headerFilePath);

		} else if (tagName == footerTagName) {

			footerCreatedDocument = createDocument(tempDocNameValue, tempDocExtensionValue, tempcontentService);
			footerFilePath = tempcontentService.getInternalFilename(footerCreatedDocument);
			headerdoc.save(footerFilePath);

		} else if (tagName == embedBodyTagName) {

			embedBodyCreatedDocument = createDocument(tempDocNameValue, tempDocExtensionValue, tempcontentService);
			embedBodyFilePath = tempcontentService.getInternalFilename(embedBodyCreatedDocument);
			headerdoc.save(embedBodyFilePath);

		}

	}

	public final static class FindAndInsertHtml implements IReplacingCallback {
		public int replacing(ReplacingArgs e) throws Exception {
			Node currentNode = e.getMatchNode();

			DocumentBuilder builder = new DocumentBuilder((com.aspose.words.Document) e.getMatchNode().getDocument());
			builder.moveTo(currentNode);
			builder.insertHtml(e.getReplacement());

			currentNode.remove();

			return ReplaceAction.SKIP;
		}
	}

	public static boolean empty(final String s) {
		// Null-safe, short-circuit evaluation.
		return s == null || s.trim().isEmpty();
	}

	public void onSave(MessageContainer messages) {
	}

	public void validate(MessageContainer msg) {

		Document doc = new Document();

		try {
			doc = contentService.download(wordDocument, ContentConstants.VERSION_CURRENT, false)[0];
		} catch (InvalidContentException | InvalidVersionException | PrivilegeException e) {
			msg.addError("wordDocument", "wordDocument.invalid", e.getMessage());
		}
		String extension = doc.getExtension();
		if (!extension.equalsIgnoreCase("doc")) {
			msg.addError("wordDocument", "wordDocument.invalidextension");
		}

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

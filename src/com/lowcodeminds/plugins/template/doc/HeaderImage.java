package com.lowcodeminds.plugins.template.doc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.appiancorp.suiteapi.content.ContentConstants;
import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.knowledge.Document;
import com.appiancorp.suiteapi.process.exceptions.SmartServiceException;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.HeaderFooter;
import com.aspose.words.HeaderFooterType;
import com.aspose.words.Node;
import com.aspose.words.NodeCollection;
import com.aspose.words.NodeType;
import com.aspose.words.PageSetup;
import com.aspose.words.ParagraphAlignment;
import com.aspose.words.RelativeHorizontalPosition;
import com.aspose.words.RelativeVerticalPosition;
import com.aspose.words.Section;
import com.aspose.words.WrapType;
import com.lowcodeminds.plugins.template.utils.PluginContext;
import com.lowcodeminds.plugins.template.utils.TemplateServices;

public class HeaderImage extends TemplatePage {


	private final String headerFooterText = "LCMHF";
	private boolean headerFooterFlag = false;

	Long headerCreatedDocument;

	private static final Log LOG = LogFactory.getLog(HeaderImage.class);

	public HeaderImage(ContentService contentService, PluginContext context, com.aspose.words.Document doc,
			ContentService tmpContentService) {
		super(contentService, context, doc, tmpContentService);
	}

	@Override
	protected void applyTemplating() throws SmartServiceException {

		Long headerImage = context.getHeaderImage();
		String headerImageFileName;

		try {
			if (headerImage != null) {

				Document headerImg = contentService.download(headerImage, ContentConstants.VERSION_CURRENT, false)[0];
				headerImageFileName = headerImg.getInternalFilename();
				LOG.info("Processing  headerImageFileName Image" + headerImageFileName);
				if (!empty(headerImageFileName)) {

					// Start Header Footer Flag Check

					Section section = doc.getFirstSection();
					int count = 0;
					for (Node node : (NodeCollection<Node>) section.getChildNodes()) {
						// Every node has the NodeType property.

						if (node.getNodeType() == NodeType.HEADER_FOOTER) {

							// headerFooterFlag = true;
							count++;
							HeaderFooter headerFooter = (HeaderFooter) node;
							if (count == 2) {
								LOG.debug("headerFooter.getText() : " + headerFooter.getText());
								// firstFooterText = headerFooter.getText();

							}
							if (headerFooterText.equals(headerFooter.getText().trim())) {

								headerFooterFlag = true;
								// headerFooter.getText().replaceAll(headerFooterText, "");
								LOG.debug("headerFooter.getText() : " + headerFooter.getText());
														}
						}

					}
					// End Header Footer Flag Check

					if (headerFooterFlag) {
					
						DocumentBuilder builder = new DocumentBuilder(doc);
						Section currentSection = builder.getCurrentSection();
						PageSetup pageSetup = currentSection.getPageSetup();
						currentSection.getPageSetup().setDifferentFirstPageHeaderFooter(true);

						builder.moveToHeaderFooter(HeaderFooterType.HEADER_FIRST);
						// builder.insertImage(headerImage, RelativeHorizontalPosition.PAGE, 10,
						// RelativeVerticalPosition.PAGE, 10,650, 50, WrapType.THROUGH);
						builder.insertImage(headerImageFileName, RelativeHorizontalPosition.PAGE, 10,
								RelativeVerticalPosition.PAGE, 10, 450, 40, WrapType.THROUGH);
						
						builder.getParagraphFormat().setAlignment(ParagraphAlignment.RIGHT);

					}else {
						LOG.info("Template doesn't  support header Image. No " + headerFooterText + " found ");
					}
				}

			}
		} catch (Exception e) {
			context.setErrorOccured(true);
			context.setErrorMessage("Error when processing header image");
			LOG.error("Exception in Header Image processing " ,e);
			throw TemplateServices.createException(e, getClass());

		}
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub

	}

}

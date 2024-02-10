package com.lowcodeminds.plugins.template.doc;

import java.io.InputStream;

import org.apache.log4j.Logger;

import com.appiancorp.suiteapi.content.ContentConstants;
import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.knowledge.Document;
import com.appiancorp.suiteapi.process.exceptions.SmartServiceException;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.FindReplaceOptions;
import com.aspose.words.HeaderFooter;
import com.aspose.words.HeaderFooterType;
import com.aspose.words.Node;
import com.aspose.words.NodeCollection;
import com.aspose.words.NodeType;
import com.aspose.words.PageSetup;
import com.aspose.words.RelativeHorizontalPosition;
import com.aspose.words.RelativeVerticalPosition;
import com.aspose.words.Section;
import com.aspose.words.WrapType;

import com.lowcodeminds.plugins.template.utils.PluginContext;
import com.lowcodeminds.plugins.template.utils.TemplateServices;

public class FooterImage extends TemplatePage {

	private final String headerFooterText = "LCMHF";
	private boolean headerFooterFlag = false;

	private static final Logger LOG = Logger.getLogger(FooterImage.class);

	public FooterImage(ContentService contentService, PluginContext context, com.aspose.words.Document doc,
			ContentService tmpContentService) {
		super(contentService, context, doc, tmpContentService);
	}

	@Override
	protected void applyTemplating() throws SmartServiceException {

		Long footerImage = context.getFooterImage();
		String footerImageFileName;
		String firstFooterText = "";

		String finalFooterText = "";

		try {
			if (footerImage != null) {

				Document footerImg = contentService.download(footerImage, ContentConstants.VERSION_CURRENT, false)[0];
				try (InputStream is = footerImg.getInputStream();) {

					if (is != null) {

						// Start Header Footer Flag Check

						Section section = doc.getFirstSection();
						int count = 0;
						for (Node node : (NodeCollection<Node>) section.getChildNodes()) {
							// Every node has the NodeType property.

							if (node.getNodeType() == NodeType.HEADER_FOOTER) {

								// headerFooterFlag = true;
								count++;
								HeaderFooter headerFooter = (HeaderFooter) node;
								// firstFooterText = headerFooter.getText().trim();
								// if (count == 2) {
								// LOG.info("headerFooter.getText() : " + headerFooter.getText());
								firstFooterText = headerFooter.getText();

								// }
								LOG.info(count + " :" + headerFooter.getText());
								if (headerFooterText.equals(headerFooter.getText().trim())) {
									headerFooterFlag = true;

								}
								// Expected footer text is base template name and it is always start with /
								if (firstFooterText.startsWith("/")) {
									firstFooterText = headerFooter.getText().trim();
									LOG.info("Found Footer Text :" + firstFooterText);
								}
							}

						}
						// End Header Footer Flag Check

						String primaryFooterText = "";
						for (Section s : doc.getSections()) {
							HeaderFooter footer = s.getHeadersFooters()
									.getByHeaderFooterType(HeaderFooterType.FOOTER_PRIMARY);
							if (footer != null) {

								LOG.info("FOOTER :" + footer.getText());
								primaryFooterText = footer.getText();

							}
						}

						if (!primaryFooterText.startsWith("/")) {
							finalFooterText = firstFooterText;
						} else
							finalFooterText = primaryFooterText;

						if (headerFooterFlag) {
							LOG.info("start Processing  footer Image");
							DocumentBuilder builder = new DocumentBuilder(doc);
							Section currentSection = builder.getCurrentSection();
							PageSetup pageSetup = currentSection.getPageSetup();
							currentSection.getPageSetup().setDifferentFirstPageHeaderFooter(true);

							// Footer Part
							pageSetup.setFooterDistance(73); // Newly Added
							builder.moveToHeaderFooter(HeaderFooterType.FOOTER_FIRST);
							builder.insertImage(is, RelativeHorizontalPosition.PAGE, 50, RelativeVerticalPosition.PAGE,
									772, 500, 50, WrapType.TOP_BOTTOM);

							if (firstFooterText != "") {
								LOG.info("Going to Add footer text ");

								doc.getRange().replace(finalFooterText, " ", new FindReplaceOptions());
								builder.getFont().setSize(8);
								builder.write(finalFooterText.trim());
								LOG.info("Footer text added " + finalFooterText);

							}

							if (context.isGenerateFooterImageAllPage() == true) {
								try (InputStream insFooter = footerImg.getInputStream();) {
									LOG.debug("Applying  footer for all pages");
									builder.moveToHeaderFooter(HeaderFooterType.FOOTER_PRIMARY);
									builder.insertImage(insFooter, RelativeHorizontalPosition.PAGE, 50,
											RelativeVerticalPosition.PAGE, 780, 500, 50, WrapType.TOP_BOTTOM);
								}
							} else {
								// HeaderFooter primaryHeader =
								// currentSection.getHeadersFooters().getByHeaderFooterType(HeaderFooterType.FOOTER_PRIMARY);
								// if(primaryHeader !=null)
								// primaryHeader.getParagraphs().clear();
							}

							LOG.debug("Footer Image successfully Inserted");
						} else {
							LOG.info("Template doesn't  support footer Image. No " + headerFooterText + " found ");
						}
					}

				}
			}
		} catch (Exception e) {
			context.setErrorOccured(true);
			context.setErrorMessage("Error when processing header image");
			LOG.error("Exception in Footer Image processing ", e);
			throw TemplateServices.createException(e, getClass());

		}
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub

	}

}

package com.lowcodeminds.plugins.tags;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.appiancorp.suiteapi.process.exceptions.SmartServiceException;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.FindReplaceOptions;
import com.aspose.words.Font;
import com.aspose.words.IReplacingCallback;
import com.aspose.words.Node;
import com.aspose.words.NodeCollection;
import com.aspose.words.NodeType;
import com.aspose.words.ReplaceAction;
import com.aspose.words.ReplacingArgs;
import com.aspose.words.Run;

import com.lowcodeminds.plugins.template.utils.PluginContext;
import com.lowcodeminds.plugins.template.utils.TemplateServices;

public class HTMLTag extends Tag {

	private final String headerFooterText = "LCMHF";
	
	private static final Logger LOG = Logger.getLogger(HTMLTag.class);

	public HTMLTag(com.aspose.words.Document doc, PluginContext context, String tagName) {
		super(doc, context, tagName);
	}

	@Override
	public void applyTemplate() throws SmartServiceException {

		try {
			for (int x = 0; x < fieldValues.length; x++) {
				String value = fieldValues[x];
				String field = fieldNames[x];
               	if (value.contains("font")) {
					FindReplaceOptions findReplaceOptions = new FindReplaceOptions();
					{
						findReplaceOptions.setReplacingCallback(new FindAndInsertHtml());
					}
					doc.getRange().replace(field, value, findReplaceOptions);
				} else {
					String fontName = null;
					Double fontSize = null;
					// Traverse through the document to find and replace all occurrences
					@SuppressWarnings("unchecked")
					NodeCollection<Run> runs = doc.getChildNodes(NodeType.RUN, true);
					for (Run run : runs) {
						String runText = run.getText();

						FindReplaceOptions findReplaceOptions = new FindReplaceOptions();
						if (runText.contains(headerFooterText)) {
							LOG.debug("runText : " + runText);
							doc.getRange().replace(headerFooterText, "", findReplaceOptions);
						}

						while (runText.contains(field)) {
							// System.out.println("while true : " + run.getFont());
							Font font = run.getFont();
							fontName = font.getName();
							fontSize = font.getSize();

							org.jsoup.nodes.Document document = Jsoup.parse(value);
							Elements body = document.getElementsByTag("body");
							fontSize = fontSize / 0.75;
							String fontStructure = "font-family:" + fontName + ";" + "font-size:" + fontSize + "px;";

							for (Element bd : body) {
								body.attr("style", fontStructure);
							}

							findReplaceOptions.setReplacingCallback(new FindAndInsertHtml());
							doc.getRange().replace(field, document.html(), findReplaceOptions);
							break;
						}

					}

				}

			}
		
		} catch (Exception e) {

			context.setErrorOccured(true);
			context.setErrorMessage("Error when processing HTML Tag ");
			LOG.error("Exception in HTML tag processing " ,e);
			throw TemplateServices.createException(e, getClass());

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

}

package com.lowcodeminds.plugins.template.utils;

public class PluginContext {
	
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
	private boolean errorOccured = false;
	private String errorMessage;
	private boolean isGenerateHeaderImageAllPage;
	
	private boolean isGenerateFooterImageAllPage;
	
	
	private Long newGeneratedDocument;
	private Long newPDFGeneratedDocument;
	
	private Long[] encloserDocuments;
	
	public Long getWordDocument() {
		return wordDocument;
	}
	public void setWordDocument(Long wordDocument) {
		this.wordDocument = wordDocument;
	}
	public String getJsonFormatTags() {
		return jsonFormatTags;
	}
	public void setJsonFormatTags(String jsonFormatTags) {
		this.jsonFormatTags = jsonFormatTags;
	}
	public Long[] getHeaderDocuments() {
		return headerDocuments;
	}
	public void setHeaderDocuments(Long[] headerDocuments) {
		this.headerDocuments = headerDocuments;
	}
	public Long[] getFooterDocuments() {
		return footerDocuments;
	}
	public void setFooterDocuments(Long[] footerDocuments) {
		this.footerDocuments = footerDocuments;
	}
	public Long[] getEmbedBodyDocuments() {
		return embedBodyDocuments;
	}
	public void setEmbedBodyDocuments(Long[] embedBodyDocuments) {
		this.embedBodyDocuments = embedBodyDocuments;
	}
	public Long getHeaderImage() {
		return headerImage;
	}
	public void setHeaderImage(Long headerImage) {
		this.headerImage = headerImage;
	}
	public Long getFooterImage() {
		return footerImage;
	}
	public void setFooterImage(Long footerImage) {
		this.footerImage = footerImage;
	}
	public Long getLicenseFile() {
		return licenseFile;
	}
	public void setLicenseFile(Long licenseFile) {
		this.licenseFile = licenseFile;
	}
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	public String getColumnText() {
		return columnText;
	}
	public void setColumnText(String columnText) {
		this.columnText = columnText;
	}
	public Boolean getIsPDFGenerate() {
		return isPDFGenerate;
	}
	public void setIsPDFGenerate(Boolean isPDFGenerate) {
		this.isPDFGenerate = isPDFGenerate;
	}
	public Long getSaveInFolder() {
		return saveInFolder;
	}
	public void setSaveInFolder(Long saveInFolder) {
		this.saveInFolder = saveInFolder;
	}
	public boolean isErrorOccured() {
		return errorOccured;
	}
	public void setErrorOccured(boolean errorOccured) {
		this.errorOccured = errorOccured;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public Long getNewGeneratedDocument() {
		return newGeneratedDocument;
	}
	public void setNewGeneratedDocument(Long newGeneratedDocument) {
		this.newGeneratedDocument = newGeneratedDocument;
	}
	public Long getNewPDFGeneratedDocument() {
		return newPDFGeneratedDocument;
	}
	public void setNewPDFGeneratedDocument(Long newPDFGeneratedDocument) {
		this.newPDFGeneratedDocument = newPDFGeneratedDocument;
	}
	
	public boolean isGenerateHeaderImageAllPage() {
		return isGenerateHeaderImageAllPage;
	}
	public void setGenerateHeaderImageAllPage(boolean isGenerateHeaderImageAllPage) {
		this.isGenerateHeaderImageAllPage = isGenerateHeaderImageAllPage;
	}
	public boolean isGenerateFooterImageAllPage() {
		return isGenerateFooterImageAllPage;
	}
	public void setGenerateFooterImageAllPage(boolean isGenerateFooterImageAllPage) {
		this.isGenerateFooterImageAllPage = isGenerateFooterImageAllPage;
	}
	public Long[] getEncloserDocuments() {
		return encloserDocuments;
	}
	public void setEncloserDocuments(Long[] encloserDocuments) {
		this.encloserDocuments = encloserDocuments;
	}
	
	

}

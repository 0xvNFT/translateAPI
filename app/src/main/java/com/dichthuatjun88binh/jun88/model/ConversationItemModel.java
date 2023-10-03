package com.dichthuatjun88binh.jun88.model;

public class ConversationItemModel {
    public int messageType;
    String sourceText;
    String targetText;
    String sourceLanguageSpeechCode;
    String targetLanguageSpeechCode;

    public ConversationItemModel(String sourceText, String targetText, int messageType, String sourceLanguageSpeechCode, String targetLanguageSpeechCode) {
        this.sourceText = sourceText;
        this.targetText = targetText;
        this.messageType = messageType;
        this.sourceLanguageSpeechCode = sourceLanguageSpeechCode;
        this.targetLanguageSpeechCode = targetLanguageSpeechCode;
    }

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public String getTargetText() {
        return targetText;
    }

    public void setTargetText(String targetText) {
        this.targetText = targetText;
    }

    public int getMessageType() {
        return messageType;
    }

    public String getTargetLanguageSpeechCode() {
        return targetLanguageSpeechCode;
    }

    public void setTargetLanguageSpeechCode(String targetLanguageSpeechCode) {
        this.targetLanguageSpeechCode = targetLanguageSpeechCode;
    }
}

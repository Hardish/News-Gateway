package com.example.newsgateway;

import java.io.Serializable;

public class Article implements Serializable {
    private String authorName;
    private String articleTitle;
    private String articleDescription;
    private String articlrURL;
    private String urlToImage;
    private String articleDate;

    public Article(String authorName, String articleTitle, String articleDescription, String articlrURL, String urlToImage, String articleDate) {
        this.authorName = authorName;
        this.articleTitle = articleTitle;
        this.articleDescription = articleDescription;
        this.articlrURL = articlrURL;
        this.urlToImage = urlToImage;
        this.articleDate = articleDate;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getArticleDescription() {
        return articleDescription;
    }

    public void setArticleDescription(String articleDescription) {
        this.articleDescription = articleDescription;
    }

    public String getArticlrURL() {
        return articlrURL;
    }

    public void setArticlrURL(String articlrURL) {
        this.articlrURL = articlrURL;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getArticleDate() {
        return articleDate;
    }

    public void setArticleDate(String articleDate) {
        this.articleDate = articleDate;
    }


    @Override
    public String toString() {
        return "Article{" +
                "authorName='" + authorName + '\'' +
                ", articleTitle='" + articleTitle + '\'' +
                ", articleDescription='" + articleDescription + '\'' +
                ", articlrURL='" + articlrURL + '\'' +
                ", urlToImage='" + urlToImage + '\'' +
                ", articleDate='" + articleDate + '\'' +
                '}';
    }
}

package com.monitor;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article {

    private String title;
    private String date;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Article article = (Article) o;
        return title.equals(article.title) && date.equals(article.date);
    }

    @Override
    public int hashCode() {
        return title.hashCode() + date.hashCode();
    }
}

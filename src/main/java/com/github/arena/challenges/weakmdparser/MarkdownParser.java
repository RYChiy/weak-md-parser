package com.github.arena.challenges.weakmdparser;

public class MarkdownParser {

    String parse(String markdownInput) {
        if (markdownInput == null) {
            return "";
        }

        String[] markdownLines = markdownInput.split("\n");
        StringBuilder htmlOutput = new StringBuilder();
        boolean isUnorderedListActive = false;

        for (String currentRawLine : markdownLines) {
            LineType currentLineType = determineMarkdownLineType(currentRawLine);

            if (currentLineType == LineType.UNORDERED_LIST_ITEM) {
                if (!isUnorderedListActive) {
                    htmlOutput.append("<ul>");
                    isUnorderedListActive = true;
                }
            } else {
                if (isUnorderedListActive) {
                    htmlOutput.append("</ul>");
                    isUnorderedListActive = false;
                }
            }

            switch (currentLineType) {
                case HEADER:
                    htmlOutput.append(parseHeader(currentRawLine));
                    break;
                case UNORDERED_LIST_ITEM:
                    htmlOutput.append(parseListItem(currentRawLine));
                    break;
                case EMPTY:
                case PARAGRAPH:
                    htmlOutput.append(parseParagraph(currentRawLine));
                    break;
            }
        }

        if (isUnorderedListActive) {
            htmlOutput.append("</ul>");
        }

        return htmlOutput.toString();
    }


    protected String parseHeader(String markdownLine) {
        int headerLevel = 0;
        while (headerLevel < markdownLine.length() && markdownLine.charAt(headerLevel) == '#') {
            headerLevel++;
        }

        if (headerLevel == 0) {
            return null;
        }

        int contentStart = headerLevel;
        if (contentStart < markdownLine.length() && markdownLine.charAt(contentStart) == ' ') {
            contentStart++;
        }

        String content = markdownLine.substring(contentStart);
        return String.format("<h%d>%s</h%d>", headerLevel, content, headerLevel);
    }

    public String parseListItem(String markdownLine) {
        if (!markdownLine.startsWith("* ")) {
            return null;
        }

        String content = markdownLine.substring(2);
        return "<li>" + parseInlineFormatting(content) + "</li>";
    }

    public String parseParagraph(String markdownLine) {
        String formatted = parseInlineFormatting(markdownLine);
        return "<p>" + formatted + "</p>";
    }

    public String parseInlineFormatting(String inputText) {
        if (inputText == null) {
            return "";
        }

        String processedText = inputText;

        //bold
        String boldRegex = "__(.+)__";
        String boldHtmlReplacement = "<strong>$1</strong>";
        processedText = processedText.replaceAll(boldRegex, boldHtmlReplacement);

        //italics
        String italicsRegex = "_(.+)_";
        String italicsHtmlReplacement = "<em>$1</em>";
        processedText = processedText.replaceAll(italicsRegex, italicsHtmlReplacement);

        return processedText;
    }

    private LineType determineMarkdownLineType(String markdownLine) {
        if (markdownLine.isEmpty()) {
            return LineType.EMPTY;
        } else if (markdownLine.startsWith("#")) {
            return LineType.HEADER;
        } else if (markdownLine.startsWith("* ")) {
            return LineType.UNORDERED_LIST_ITEM;
        } else {
            return LineType.PARAGRAPH;
        }
    }

    private enum LineType {
        HEADER, UNORDERED_LIST_ITEM, PARAGRAPH, EMPTY
    }
}
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
        int hashSymbolCount = 0;

        for (int charIndex = 0; charIndex < markdownLine.length() && markdownLine.charAt(charIndex) == '#'; charIndex++) {
            hashSymbolCount++;
        }

        if (hashSymbolCount == 0) {
            return null;
        }

        return "<h" + hashSymbolCount + ">" + markdownLine.substring(hashSymbolCount + 1) + "</h" + hashSymbolCount + ">";
    }

    public String parseListItem(String markdownLine) {
        if (markdownLine.startsWith("*")) {
            String listItemTextContent = markdownLine.substring(2);
            String formattedListItemText = parseInlineFomatting(listItemTextContent);
            return "<li>" + formattedListItemText + "</li>";
        }

        return null;
    }

    public String parseParagraph(String markdownLine) {
        return "<p>" + parseInlineFomatting(markdownLine) + "</p>";
    }

    public String parseInlineFomatting(String inputText) {

        String regexPatternForStrong = "__(.+)__";
        String htmlFormatForStrong = "<strong>$1</strong>";

        String processedText = inputText.replaceAll(regexPatternForStrong, htmlFormatForStrong);

        String regexPatternForEmphasis = "_(.+)_";
        String htmlFormatForEmphasis = "<em>$1</em>";
        return processedText.replaceAll(regexPatternForEmphasis, htmlFormatForEmphasis);
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
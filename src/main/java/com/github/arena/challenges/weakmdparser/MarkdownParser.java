package com.github.arena.challenges.weakmdparser;

public class MarkdownParser {

    String parse(String markdownInput) {
        String[] markdownLines = markdownInput.split("\n");
        String htmlOutput = "";
        boolean isUnorderedListActive = false;

        for (int lineIndex = 0; lineIndex < markdownLines.length; lineIndex++) {


            String currentRawLine = markdownLines[lineIndex];

            String parsedLineContent = parseHeader(currentRawLine);

            if (parsedLineContent == null) {
                parsedLineContent = parseListItem(currentRawLine);
            }

            if (parsedLineContent == null) {
                parsedLineContent = parseParagraph(currentRawLine);
            }

            if (parsedLineContent.matches("(<li>).*") && !parsedLineContent.matches("(<h).*") && !parsedLineContent.matches("(<p>).*") && !isUnorderedListActive) {
                isUnorderedListActive = true;
                htmlOutput = htmlOutput + "<ul>";
                htmlOutput = htmlOutput + parsedLineContent;
            } else if (!parsedLineContent.matches("(<li>).*") && isUnorderedListActive) {
                isUnorderedListActive = false;
                htmlOutput = htmlOutput + "</ul>";
                htmlOutput = htmlOutput + parsedLineContent;
            } else {
                htmlOutput = htmlOutput + parsedLineContent;
            }
        }

        if (isUnorderedListActive) {
            htmlOutput = htmlOutput + "</ul>";
        }

        return htmlOutput;
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
}
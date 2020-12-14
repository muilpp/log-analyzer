/** highlighter.css
    place in same directory as ListViewTextHighlighter.java
    ensure build system copies the css file to the build output path */

.root {
    -fx-background-color: cornsilk;
    -fx-padding: 10;
}

/** Remove default highlighting of alternate rows */

.list-cell:odd {
    -fx-background-color: -fx-control-inner-background;
}

.list-view:focused .list-cell:focused:odd {
    -fx-background-color: -fx-focus-color, -fx-cell-focus-inner-border, -fx-control-inner-background;
}

/** Highlighting for list-view search result cells */

.list-cell.search-highlight {
    -fx-background-color: tomato;
    -fx-accent: firebrick;
}

.list-cell:filled:hover.search-highlight {
    -fx-background-color: derive(tomato, -20%);
}

/** As we have overridden the background-color things don't seem to work to well unless we copy the standard styles for
    list-view color highlighting, otherwise we lose all of that functionality */

.list-view:focused .list-cell:filled:selected, .list-view:focused .list-cell:filled:selected:hover {
    -fx-background-color: -fx-selection-bar;
}

.list-view:focused .list-cell:filled:focused:selected:hover {
    -fx-background-color: -fx-focus-color, -fx-cell-focus-inner-border, -fx-selection-bar;
}

.list-cell:filled:selected:focused, .list-cell:filled:selected, .list-view:horizontal .list-cell:filled:selected {
    -fx-background-color: lightgray;
}

.list-cell:filled:hover {
    -fx-background-color: -fx-cell-hover-color;
}

.list-view:focused .list-cell:filled:focused:hover {
    -fx-background-color: -fx-focus-color, -fx-cell-focus-inner-border, -fx-cell-hover-color;
}

.list-view:horizontal .list-cell:filled:selected, .list-view:horizontal .list-cell:filled:selected:hover {
    -fx-background-color: linear-gradient(to right, derive(-fx-accent,-7%), derive(-fx-accent,-25%));
}
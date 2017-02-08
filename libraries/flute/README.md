The grammar of the parser has been changed to be a little bit more suitable
for CSS3.

The @page-rule grammer given in the CSS3-Page module was funny, but unusable.
Therefore it has been replaced by a simplicistic approach. The margin-rules
now have to follow the property declarations - mixing margins and property
declarations is no longer valid.

The margin-box rules get forwarded to the 'ignorableAtRule' method of the
Handler class - we have to maintain API level compatibility for BIRT and other
projects, which may depend on that parser.

The selector syntax is also upgraded to CSS3 - especially the pseudo-elements
were not parsed and caused trouble here. As this did not work before,
stylesheets of other programms should not be affected by this change - if they
specified things which were known not to work and start dying if it works -
well, that's bad luck, I guess.


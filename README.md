### Release Notes

https://github.com/blackducksoftware/notice-report-tool/wiki/Release-Notes

### Summary

Tool generates a HTML Notice file based on the identifications made for the project.

The output will&nbsp;contain an entry for each BOM Component. Each entry will have links to display:

1.  &nbsp;All paths ID-ed to the Component (if option is selected)
2.  &nbsp;All copyright strings extracted from the text files identified to it (if option is selected)
3.  &nbsp;All licenses identified to the component and ability to display text

The output can be controlled by supplying custom attribute tools against your Code Center application. &nbsp;See configuration for more information.

The tool can also generate a text file for each component (if option is selected)

**To run:**

1.  Create/edit the configuration property file.
2.  Unzip the package.
3.  Navigate to /bin/
4.  Run &nbsp;NoticeReportTool (bat or sh) -config <**location of configuration file**> -application <**black duck application type**: _PROTEX_ | _CODECENTER_> -project <name of Protex project>

**Command Line Arguments**

-config: &nbsp;Location of the configuration file required for the Notice Report Tool. &nbsp;Absolute path only.

-application: &nbsp;The Black Duck application that will be used to generate the report. &nbsp;Use Code Center for attribute specific generation.

-project: &nbsp;The name of the Protex project, used instead of the configuration file 'project.name' property. &nbsp;Useful in case of automation.


set XSD_FILE=%cd%\Stepper-V1.xsd
set DEST_DIR=C:\Projects\Java\Stepper\Engine\src\com\main\stepper\xml\generated\ex1

if not exist %DEST_DIR% (
    mkdir %DEST_DIR%
)

cd %JAVA_HOME%\bin
xjc %XSD_FILE% -d %DEST_DIR%
**[ocr-redaction-api] is a PDF/IMG (PNG,TIFF,JPEG) ocr-reading/editing rest API **, it uses [iText 7] PDF Sweep and its powered by Tess4j OCR Engine.
You will need to have the native tesseract libraries installed to run the OCR extraction part properly, 
make sure you look arround Tesseract windows/linux/mac and follow instructions on [how to install][how-tesseract]

This API exposes 2 rest methods:
 - /files/text-extract -> PDF/IMG OCR text/word extraction : Powered by Tesseract JNA wrapper arround Tess4j
 - /files/text-redact ->  PDF/IMG text 'redact' on image/pdf locations : Powered by Itext Pdfsweep tool.

The [pdfSweep Community][github-pdfsweep] and  [Tess4j][github-tess4j] source code are hosted on Github.

**ocr-redaction-api** is  licensed as [AGPL][agpl].

AGPL is a free / open source software license.

[agpl]: LICENSE.md
[github-pdfsweep]: https://github.com/itext/i7j-pdfsweep
[github-tess4j]: https://github.com/nguyenq/tess4j
[how-tesseract]: http://tess4j.sourceforge.net/usage.html

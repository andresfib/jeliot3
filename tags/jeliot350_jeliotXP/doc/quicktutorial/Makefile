CC = latex
PDFCC = dvipdf

DEP = quicktutorial.tex
DEPDVI = quicktutorial.dvi

all: dvi pdf 

dvi: ${DEP}
	${CC} ${DEP}
	${CC} ${DEP}

pdf: ${DEPDVI}
	${PDFCC} ${DEPDVI}

clean:
	rm -f *.aux *.dvi *.log *.pdf
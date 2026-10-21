import java.util.Locale;

public class Logic {

    private int intNumSeg;
    private double dblE;
    private int intDOF;
    private double dblX;

    // Ejecuta las 3 pruebas de la Tabla 1 y guarda los resultados en un archivo
    public void logic1a() {
        double[] xs = {1.1, 1.1812, 2.750};
        int[] dofs = {9, 10, 30};
        double[] expected = {0.35006, 0.36757, 0.49500};

        output out = new output();
        StringBuilder text = new StringBuilder();
        text.append("Test | x | dof | Expected p | Actual p | num_seg final\n");

        for (int t = 0; t < xs.length; t++) {
            dblX = xs[t];
            intDOF = dofs[t];
            dblE = 0.00001;
            intNumSeg = 10;

            double result = integrate();

            String line = String.format(Locale.US,
                "%d | %.4f | %d | %.5f | %.5f | %d%n",
                t + 1, dblX, intDOF, expected[t], result, intNumSeg);
            text.append(line);
        }

        System.out.print(text);
        out.writeData("resultados.txt", text.toString());
    }

    // Duplica num_seg hasta que la diferencia entre dos resultados sea menor que E
    private double integrate() {
        SimpsonIntegration s = new SimpsonIntegration(intDOF, dblX, dblE);

        int n = intNumSeg;
        double previous = simpsonValue(s, n);
        n = n * 2;
        double current = simpsonValue(s, n);

        while (Math.abs(current - previous) >= dblE) {
            previous = current;
            n = n * 2;
            current = simpsonValue(s, n);
        }

        intNumSeg = n;
        return current;
    }

    // Corre todos los pasos de Simpson con n segmentos y devuelve el valor final
    private double simpsonValue(SimpsonIntegration s, int n) {
        s.computeW(n, dblX);
        s.computeXi(n);
        s.computeFirstBaseTerms(n, s.getTotXi(), intDOF);
        s.computeExponent(intDOF);
        s.computeCoefficient(intDOF);
        s.computeFxi(n);
        s.computeFinalTerms(n);
        s.computeFinalValue(n);
        return s.getFinalValue();
    }
}
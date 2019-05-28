package ovh.asetniew.ba.fitting;

import java.util.*;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.AbstractCurveFitter;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;

/**
 *
 * Power Distibution
 * Credits for fitting methods: i80and and tvicky4j247
 * from: https://stackoverflow.com/questions/11335127/how-to-use-java-math-commons-curvefitter
 */
class PowerDistribution implements ParametricUnivariateFunction {
    public double value(double t, double... parameters) {
        // A*t^(B)
        return parameters[0] * Math.pow(t, parameters[1]);
    }

    public double[] gradient(double t, double... parameters) {
        final double a = parameters[0];
        final double b = parameters[1];

        DerivativeStructure aDev = new DerivativeStructure(2, 1, 0, a);
        DerivativeStructure bDev = new DerivativeStructure(2, 1, 1, b);

        DerivativeStructure y = aDev.multiply(DerivativeStructure.pow(t, bDev));

        return new double[] {
                y.getPartialDerivative(1, 0),
                y.getPartialDerivative(0, 1),
        };

    }
}

public class PowerDistributionFitter extends AbstractCurveFitter {
    protected LeastSquaresProblem getProblem(Collection<WeightedObservedPoint> points) {
        final int len = points.size();
        final double[] target  = new double[len];
        final double[] weights = new double[len];
        final double[] initialGuess = { 1.0, 1.0};

        int i = 0;
        for(WeightedObservedPoint point : points) {
            target[i]  = point.getY();
            weights[i] = point.getWeight();
            i += 1;
        }

        final AbstractCurveFitter.TheoreticalValuesFunction model = new
                AbstractCurveFitter.TheoreticalValuesFunction(new PowerDistribution(), points);

        return new LeastSquaresBuilder().
                maxEvaluations(Integer.MAX_VALUE).
                maxIterations(Integer.MAX_VALUE).
                start(initialGuess).
                target(target).
                weight(new DiagonalMatrix(weights)).
                model(model.getModelFunction(), model.getModelFunctionJacobian()).
                build();
    }

}
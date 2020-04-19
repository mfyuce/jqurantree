package org.jqurantree.analysis.meka;

import meka.classifiers.multilabel.Evaluation;
import meka.classifiers.multitarget.meta.BaggingMT;
import meka.core.Result;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.jqurantree.tools.Tools.writeFile;

/**
 * https://sourceforge.net/p/meka/mailman/message/32332681/
 */
public class MekaModelLoader
{
     /* @param args command-line arguments -train, -test and -model, e.g.
-train Flags-train.arff -test Flags-test.arff -model model.dat
*/
    public static void main(String[] args)
    {
        try
        {
            Evaluation evaluator=new Evaluation();
            Result result=new Result();
            String trainingDataFilename = Utils.getOption("train", args);
            String testingDataFilename = Utils.getOption("test", args);
            String modelFilename = Utils.getOption("model", args);
            System.out.println(trainingDataFilename);

            System.out.println("Load the test data");
            DataSource sourceTest = new DataSource(testingDataFilename);
            Instances testingData = sourceTest.getDataSet();

            System.out.println("Load the model");
            BaggingMT learner2;

            learner2 = (BaggingMT) SerializationHelper.read(modelFilename);
            testingData.setClassIndex(3);
            System.out.println("Test the model");
            result =evaluator.testClassifier(learner2,testingData);

            System.out.println("Save performance");
            System.out.println(result.toString());
            StringBuilder sb = new StringBuilder();
            for (int[] i:result.actuals) {
                for (int ii:i) {
                    sb.append(ii+",");
                }
                sb.append("\n");
            }
            writeFile("actuals.csv", sb.toString());

            sb = new StringBuilder();
            for (double[] i:result.predictions) {
                for (double ii:i) {
                    sb.append(ii+",");
                }
                sb.append("\n");
            }
            writeFile("predictions.csv", sb.toString());
        }
        catch (Exception ex)
        {
            Logger.getLogger(MekaModelLoader.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }
}

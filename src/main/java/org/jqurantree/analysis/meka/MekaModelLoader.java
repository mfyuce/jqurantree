package org.jqurantree.analysis.meka;

import meka.classifiers.multilabel.Evaluation;
import meka.classifiers.multitarget.meta.BaggingMT;
import meka.core.Result;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.jqurantree.tools.Tools.getAllDistinctLetters;
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
//            String trainingDataFilename = Utils.getOption("train", args);
            String testingDataFilename = Utils.getOption("test", args);
            String modelFilename = Utils.getOption("model", args);
//            System.out.println(trainingDataFilename);

            System.out.println("Load the test data");
            DataSource sourceTest = new DataSource(testingDataFilename);
            Instances testingData = sourceTest.getDataSet();

            System.out.println("Load the model");
            BaggingMT learner2;

            learner2 = (BaggingMT) SerializationHelper.read(modelFilename);
            testingData.setClassIndex(3);
            System.out.println("Test the model");
//            Evaluation.runExperiment(learner2,new String[]{
//                    "-T",testingDataFilename,
//                    "-predictions", "predictions_out.csv",
//                    "-verbosity", "7"
//            });
            result = Evaluation.evaluateModel(learner2,testingData,"0.5","7");
//            result =evaluator.testClassifier(learner2,testingData);
//            result.setInfo("Type","ML");
//            result.setInfo("Threshold","0.5");
//            result.setInfo("Verbosity","7");
//            result.output = Result.getStats(result, "7");
//            System.out.println((Double)result.getMeasurement(m_Payoff));
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
            //.value(0)
            Attribute attribute0 = testingData.attribute(0);
            Attribute attribute1 = testingData.attribute(1);
            Attribute attribute2 = testingData.attribute(2);
            String[] allLetters =  getAllDistinctLetters(true);
            StringBuilder sbRoots = new StringBuilder();
            for (double[] i:result.predictions) {
                int indexLetter = Integer.parseInt(attribute0.value((int) i[0]).substring(1));
                sbRoots.append(allLetters[indexLetter-1] + "");
                indexLetter = Integer.parseInt(attribute1.value((int) i[1]).substring(1));
                sbRoots.append(allLetters[indexLetter-1] + "");
                indexLetter = Integer.parseInt(attribute2.value((int) i[2]).substring(1));
                sbRoots.append(allLetters[indexLetter-1] + "");
                sbRoots.append("\n");
            }
            writeFile("predictions_words.csv", sbRoots.toString());
        }
        catch (Exception ex)
        {
            Logger.getLogger(MekaModelLoader.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }
}

package org.wildstang.year2023.subsystems.arm;
import org.javatuples.Triplet;

public class Network { 
    private double a = 1.0;
    private double[] dot(double[] a, double[] b){
        int c = 0;
        double out = 0;
        while(c<a.length){
            out += a[c]*b[c];
            c += 1;
        }
        return out
    }
    private double neuronForward(double[] weights,double bias, double[] inp){ //using ReLu activation function (b/c its easy. Idk if the performance is actually any good)
        double out = Network.dot(weights,inp)+bias;
        if(out<0){
            out = a*math.exp(z)-1;
        }
        return out
    }
    private Triplet<double[],double[],double> neuronBackward(double[] weights,double bias, double[] inp, double DeDo,double learn){
        z = neuronForward(weights,bias,inp);
        if(z>0){
            DeDo = DeDo;
        }
        else{
            DeDo = DeDo*(1+z);
        }
        int c = 0;
        double[weights.length] DeDi;
        while(c<weights.length){
            DeDi[c] = (DeDo*weights[c]);
            weights[c] = weights[c]-(learn*DeDo*inp[c]);
            c += 1;
        }
        bias = bias - (learn*DeDo);
        return new Triplet<double[],double[],double>(DeDi,weights,bias)
    }
    
    private Triplet<double[],double[][],double[]> layerBackward(double[][] weights, double[] bias, double[] inp, double[] DeDo, double learn){
        int c = 0;
        double[inp.length] out;
        while(c<weights.length){
            double[inp.length] stuff;
            stuff,weights[c],bias[c] = neuronBackward(weights[c],bias[c],inp,DeDo[c],learn);
            int c2 = 0;
            while(c2<stuff.length):
                out[c] += stuff[c];
                c2 += 1;
            c += 1;
        }
        return new Triplet<double[],double[][],double[]>(out,weights,bias)
    }
}
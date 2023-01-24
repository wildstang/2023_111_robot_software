package org.wildstang.year2023.subsystems.arm;
//import java.io.math;
public class Network { 
    private double a = 1.0;
    private double dot(double[] a, double[] b){
        int c = 0;
        double out = 0;
        while(c<a.length){
            out += a[c]*b[c];
            c += 1;
        }
        return out;
    }
    private double neuronForward(double[] weights,double bias, double[] inp){ //using ReLu activation function (b/c its easy. Idk if the performance is actually any good)
        double out = dot(weights,inp)+bias;
        if(out<0){
            out = a*Math.exp(out)-1;
        }
        return out;
    }
    private double[][] neuronBackward(double[] weights,double bias, double[] inp, double DeDo,double learn){
        double z = neuronForward(weights,bias,inp);
        if(z>0){
            DeDo = DeDo;
        }
        else{
            DeDo = DeDo*(1+z);
        }
        int c = 0;
        double[] DeDi = new double[weights.length];
        while(c<weights.length){
            DeDi[c] = (DeDo*weights[c]);
            weights[c] = weights[c]-(learn*DeDo*inp[c]);
            c += 1;
        }
        bias = bias - (learn*DeDo);
        double[][] rData = {DeDi,weights,{bias}};
        return rData;
    }
    
    private double[][][] layerBackward(double[][] weights, double[] bias, double[] inp, double[] DeDo, double learn){
        int c = 0;
        double[] out = new double[inp.length];
        while(c<weights.length){
            double[] stuff = new double[inp.length];
            double[][] ugh = neuronBackward(weights[c],bias[c],inp,DeDo[c],learn);
            stuff = ugh[0];
            weights[c] = ugh[1];
            bias[c] = ugh[2][0];
            int c2 = 0;
            while(c2<stuff.length){
                out[c2] += stuff[c2];
                c2 += 1;
            }
            c += 1;
        }
        double[][][] rData = {{out},weights,{bias}};
        return rData;
    }
    private double[] layerForward(double[][] weights,double[] bias, double[] inp){
        double[] out = new double[weights.length];
        int c = 0;
        while(c<weights.length){
            out[c] = neuronForward(weights[c],bias[c],inp);
            c += 1;
        }
        return out;
    }
}
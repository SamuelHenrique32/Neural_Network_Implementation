// Activation function
function sigmoid(value){

    // Aplies sigmoid
    return (1)/(1+Math.exp(-value));
}

class NeuralNetwork{

    constructor(entryNeurons, intermediaryNeurons, outputNeurons){

        this.entryNeurons = entryNeurons;
        this.intermediaryNeurons = intermediaryNeurons;
        this.outputNeurons = outputNeurons;

        // One column
        this.biasEntryIntermediary = new Matrix(this.intermediaryNeurons, 1);
        this.biasEntryIntermediary.randomize();
        this.biasIntermediaryOutput = new Matrix(this.outputNeurons, 1);
        this.biasIntermediaryOutput.randomize();

        this.weightsEntryIntermediary = new Matrix(this.intermediaryNeurons, this.entryNeurons);
        this.weightsEntryIntermediary.randomize();

        this.weightsIntermediaryOutput = new Matrix(this.outputNeurons, this.intermediaryNeurons);
        this.weightsIntermediaryOutput.randomize();

        
    }

    feedForward(array){

        // ------------------------------------------------------------ 
        // Input -> intermediary        
        let input = Matrix.convertArrayToMatrix(array);
        // Multiply weight by input
        let intermediary = Matrix.multiply(this.weightsEntryIntermediary, input);
        // Add random bias
        intermediary = Matrix.add(intermediary, this.biasEntryIntermediary);        
        intermediary.map(sigmoid);
        // ------------------------------------------------------------

        // ------------------------------------------------------------ 
        // Intermediary -> output
        let output = Matrix.multiply(this.weightsIntermediaryOutput, intermediary);
        output = Matrix.add(output, this.biasIntermediaryOutput);
        output.map(sigmoid);
        return output;
        // ------------------------------------------------------------
        
    }
}
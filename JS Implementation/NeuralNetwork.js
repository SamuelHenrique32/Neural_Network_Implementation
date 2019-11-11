// Activation function
function sigmoid(value){

    // Aplies sigmoid
    return (1)/(1+Math.exp(-value));
}

function dSigmoid(value){
    
    return value*(1-value);
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

        // Common value
        this.learningRate = 0.1;
    }

    // Target is the correct values
    train(array, target){

    // Feedforward
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
        // ------------------------------------------------------------

    // End feedforward
    // ------------------------------------------------------------ 

    // Backpropagation
    // ------------------------------------------------------------ 

        // ------------------------------------------------------------ 
        // Output -> intermediary
        let expectedAnswer = Matrix.convertArrayToMatrix(target);
        let outputError = Matrix.sub(expectedAnswer, output);

        // Derivate output
        let derivatedOutput = Matrix.map(output,dSigmoid);

        let intermediaryT = Matrix.transpose(intermediary);

        let gradient = Matrix.hadamardProduct(derivatedOutput, outputError);
        gradient = Matrix.scalarMultiply(gradient, this.learningRate);

        // Adjust bias
        this.biasIntermediaryOutput = Matrix.add(this.biasIntermediaryOutput, gradient);

        // Error between two layers
        let weigthsIntermediaryOutputDeltas = Matrix.multiply(gradient, intermediaryT);
        this.weightsIntermediaryOutput = Matrix.add(this.weightsIntermediaryOutput,weigthsIntermediaryOutputDeltas);
        // ------------------------------------------------------------

        // ------------------------------------------------------------ 
        // Intermediary -> input
        let weigthsIntermediaryOutputT = Matrix.transpose(this.weightsIntermediaryOutput);
        let intermediaryError = Matrix.multiply(weigthsIntermediaryOutputT,outputError);
        let dIntermediary = Matrix.map(intermediary,dSigmoid);
        let inputT = Matrix.transpose(input);

        let gradientIntermediary = Matrix.hadamardProduct(dIntermediary, intermediaryError);
        gradientIntermediary = Matrix.scalarMultiply(gradientIntermediary, this.learningRate);

        // Adjust bias
        this.biasEntryIntermediary = Matrix.add(this.biasEntryIntermediary, gradientIntermediary);

        // Error between two layers
        let weightsEntryIntermediaryDeltas = Matrix.multiply(gradientIntermediary, inputT);
        this.weightsEntryIntermediary = Matrix.add(this.weightsEntryIntermediary, weightsEntryIntermediaryDeltas);
        
        // ------------------------------------------------------------

    // End backpropagation
    // ------------------------------------------------------------
    }

    predict(array){
    // Feedforward
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
        // ------------------------------------------------------------

        // Convert to array
        output = Matrix.convertMatrixToArray(output);
        return output;

    // End feedforward
    // ------------------------------------------------------------ 

    }
}
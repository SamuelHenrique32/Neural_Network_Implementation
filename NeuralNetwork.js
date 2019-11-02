class NeuralNetwork{

    constructor(entryNeurons, intermediaryNeurons, outputNeurons){

        this.entryNeurons = entryNeurons;
        this.intermediaryNeurons = intermediaryNeurons;
        this.outputNeurons = outputNeurons;

        // One column
        this.biasEntryIntermediary = new Matrix(intermediaryNeurons, 1);
        this.biasEntryIntermediary.randomize();

        this.biasIntermediaryOutput = new Matrix(outputNeurons, 1);
        this.biasIntermediaryOutput.randomize();

        this.biasEntryIntermediary.print();
        this.biasIntermediaryOutput.print();
    }
}
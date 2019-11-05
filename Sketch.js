// P5 functions

// Executed at browser setup (1x)
function setup(){

    createCanvas(500,500);
    background(0);

    // Instanciate neural network
    //var neuralNetwork = new NeuralNetwork(1,3,5);

    // Test
    //var array = [1, 2];
    
    // Call feedforward method
    //neuralNetwork.feedForward(array);

    let matrixA = new Matrix(2,1);
    let matrixB = new Matrix(2,1);

    matrixA.randomize();
    matrixB.randomize();

    matrixA.print();
    matrixB.print();
    
    let matrixC = Matrix.hadamardProduct(matrixA, matrixB);

    matrixC.print();
}

// Executed 30x/s
function draw(){

}
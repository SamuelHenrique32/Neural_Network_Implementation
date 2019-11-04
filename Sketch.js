// P5 functions

// Executed at browser setup (1x)
function setup(){

    createCanvas(500,500);
    background(0);

    // Instanciate neural network
    var neuralNetwork = new NeuralNetwork(1,3,5);

    // Test
    var array = [1, 2];
    
    // Call feedforward method
    neuralNetwork.feedForward(array);
}

// Executed 30x/s
function draw(){

}
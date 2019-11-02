// P5 functions

// Executed at browser setup (1x)
function setup(){

    createCanvas(500,500);
    background(0);

    var matrix1 = new Matrix(2,2);
    var matrix2 = new Matrix(2,2);

    // Add two matrix static method
    Matrix.add(matrix1, matrix2);
}

// Executed 30x/s
function draw(){

}
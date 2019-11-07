import React, { Component } from 'react';
import { withStyles } from "@material-ui/core/styles";
import Line from "./Line"
// Material UI
import { Grid, Button, Typography, TextField } from "@material-ui/core"
const styles = {
    button:{
        width:"100%",
        height:"100%",
        
    },
    buttonGrid:{
        minHeight:10,
        borderStyle:"solid"
    }
};
class Maze extends Component {
    constructor(props) {
        super(props);

        this.props = props;

        this.state = {
            grid: [],
            dataset:[],
            line : 0
        };
    }

    componentDidMount() {
        this.generateGrid();
    }

    generateGrid = () => {
        let grid = [];
        for (let i = 0; i < 8; i++) {
            grid.push([]);
            for (let j = 0; j < 6; j++) {
                grid[i].push(false)
            }
        }
        this.setState({ grid: grid })
    }

    onClickButton = (i,j) =>{
        let grid = this.state.grid;
        grid[i][j] = !grid[i][j];
        this.setState({ grid: grid })
    }

    saveGrid = () =>{
        let line = ""
        this.state.grid.forEach((val)=>{
            let nums = val.map((s)=>{
                if(s){
                    return "1"
                }else{
                    return "0"
                }
            })
            line = line + nums.join('')
        })
        let dataset = this.state.dataset;
        dataset.push(line);
        this.generateGrid();
    }

    //Component default methods

    //Event methods

    //Component methods

    //Store methods

    render() {
        const { classes } = this.props;
        return (
            <Grid container>
                <Grid item xs={12}>
                    <Button onClick={this.saveGrid}>Save</Button>
                </Grid>
                <Grid item xs={4}>
                    <Grid container>
                        {this.state.grid.map((val,i) => {
                            return val.map((v,j) => {
                                let color = v ? "blue" : "white"
                                return <Grid item xs={2} className={classes.buttonGrid}><Button className={classes.button} style={{backgroundColor:color}} onClick={()=>{this.onClickButton(i,j)}}></Button></Grid>
                            })
                        })}
                    </Grid>
                </Grid>
                <Grid item xs={8}>
                    {this.state.dataset.map((val)=>{
                        return <Grid container>{val}</Grid>
                    })}
                    <Grid container>
                        <Grid item xs={6}><Button>Anterior</Button></Grid>
                        <Grid item xs={6}><Button>Anterior</Button></Grid>
                    </Grid>
                </Grid>
            </Grid>
        )
    }

}

export default withStyles(styles)(Maze);
import * as React from "react";

import { FormControl,FormGroup,ControlLabel,Checkbox,Button } from "react-bootstrap";

export interface SubscribeProps { apiUrl:string; }
export interface SubscribeState {
  presentation: string; email:string; sandwich_type:string; drink:boolean;
}

export class Subscribe extends React.Component<SubscribeProps, SubscribeState> {
  constructor(props: SubscribeProps){
    super(props);
    this.state = {
      presentation:"",
      email:"",
      sandwich_type:"",
      drink: false
    };
    this.handleChanged = this.handleChanged.bind(this);
  }
  handleChanged(e:any) {
    this.setState({[e.target.name]: e.target.value});
  }
  submit(){
    fetch(this.props.apiUrl)
    .then((res)=> {
      if(res.ok){
        this.setState({
          presentation:"",
          email:"",
          sandwich_type:"",
          drink: false
        });
      }else {

      }
    });
  }
  render() {
    return (
      <form>
        <FormGroup controlId="formControlsPresentation">
          <ControlLabel>Präsentation</ControlLabel>
          <br/>
          <FormControl type="text" name="presentation" placeholder="Präsentationstitel" onChange={this.handleChanged} required/>
        </FormGroup>

        <FormGroup controlId="formControlsEmail">
          <ControlLabel>Email</ControlLabel>
          <br/>
          <FormControl type="email" name="email" placeholder="Emailadresse" onChange={this.handleChanged} required/>
        </FormGroup>
        <FormGroup controlId="formControlsSandwichType">
          <ControlLabel>Sandwichtyp</ControlLabel>
          <br/>
          <FormControl componentClass="select" name="sandwich_type" onChange={this.handleChanged} placeholder="Sandwichtyp" >
            <option value="vegi">vegi</option>
            <option value="fleisch">fleisch</option>
          </FormControl>
        </FormGroup>
        <FormGroup controlId="formControlsDrink">
          <Checkbox name="drink" onChange={this.handleChanged}>
            Getränk
          </Checkbox>
        </FormGroup>
        <Button type="submit" bsStyle="primary" onClick={this.submit}>
          Senden
        </Button>
      </form>
    );
  }
}
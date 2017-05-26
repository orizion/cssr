import * as React from "react";
import { FormControl,FormGroup,ControlLabel,Checkbox,Button } from "react-bootstrap";
import update = require('react-addons-update');
import Select = require('react-select');
import { translate } from 'react-i18next';

import * as API from "../services/api";

export interface SubscribeProps {  
  presentationId: number,
  t:any
}
export interface SubscribeState {
  subscription:API.Subscription,
  presentation:API.Presentation;
}

@translate(['subscribe', 'common'], { wait: true })
export class Subscribe extends React.Component<SubscribeProps, SubscribeState> {
  constructor(props: SubscribeProps){
    super(props);
    this.state = {
      subscription: {
        presentationId:0,
        user:undefined,
        sandwichType:"v",
        drink: ""
      },
      presentation: {
        abstract:"",
        dateTime:new Date(),
        deadline:new Date(),
        location:"",
        presentationId:0,
        speakerId:0,
        title:"",
      }
     
    };
    API.defaultHeaders["Authorization"] = "Bearer " + localStorage.token;
    this.handleChanged = this.handleChanged.bind(this);
    this.handleSelectChanged = this.handleSelectChanged.bind(this);
    this.submit = this.submit.bind(this);
  }
  userAPI = new API.UsercontrollerApi();
  subscriptionAPI = new API.SubscriptioncontrollerApi();
  t = this.props.t;
  sandwichTypes = [
    {value:"f",label:this.t("meat")},
    {value:"v",label:this.t("vegetarian")}
  ];
  handleChanged(e:any) {
    this.setState({
      subscription: update(this.state.subscription, {  
          [e.target.name]: { $set: e.target.value }
      }),
      presentation: this.state.presentation
    });
    console.log(this.state);
  }
  handleSelectChanged(e: any) {
    console.log(e);
        this.setState({
            subscription: update(this.state.subscription, {
                 sandwichType : { $set: e.value }
            }),
        });
        console.log(this.state);
        console.log(this.state.subscription);
  }
  componentDidMount() {
    let presentationAPI = new API.PresentationcontrollerApi();
    presentationAPI.getSingleUsingGET({"id":this.props.presentationId})
    .then((response: API.Presentation) => {
      this.setState({
        subscription: update(this.state.subscription, {
            presentationId : { $set: response.presentationId }
        }),
        presentation: response
      });
      console.log(this.state);
    }).catch((err) => {
      console.log(err);
    });
    this.userAPI.getUsingGET()
    .then((_user)=>{
      
    })
    .catch((err)=>{
      console.log("Getting info of current user failed");
      console.log(err);
    });
  }
  submit(e:any) {
    e.preventDefault();
    let presentationId = this.state.presentation.presentationId || -1; 
    let params = {
      "presentationId": presentationId, 
      "subscription": this.state.subscription
    };
    console.log(this.state.subscription);


    this.subscriptionAPI.addSubscriptionUsingPOST(params)
    .then((result)=>{
       console.log("Result: "+result);
    })
    .catch((err) => {
    console.log("Submitting Subscription failed with error: ");
    console.log(err);
   });
  }
  render() {
    return (
      <form>
        <FormGroup controlId="formControlsPresentation">
          <ControlLabel>Präsentation</ControlLabel>
          <br/>
          <h2>{this.state.presentation.title}</h2>
          <p>
            {this.state.presentation.abstract}
          </p>
          <FormControl type="hidden" name="presentationID" value={this.state.presentation.presentationId} />
        </FormGroup>

        <FormGroup controlId="formControlsEmail">
          <ControlLabel>Email</ControlLabel>
          <br/>
          <FormControl type="email" name="email" placeholder="Emailadresse" onChange={this.handleChanged} required/>
        </FormGroup>
        <FormGroup controlId="formControlsSandwichType">
          <ControlLabel>Sandwichtyp</ControlLabel>
          <br/>
          <Select name="fileType"     
                  value={this.state.subscription.sandwichType}
                  options={this.sandwichTypes}
                  onChange={this.handleSelectChanged}
                  clearable={false}
              />
        </FormGroup>
        <FormGroup controlId="formControlsDrink">
          <FormControl type="text" name="drink" placeholder="Getränk" onChange={this.handleChanged} required/>
        </FormGroup>
        <Button type="submit" bsStyle="primary" onClick={this.submit}>
          Einschreiben
        </Button>
      </form>
    );
  }
}

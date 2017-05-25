import * as React from "react";
import { FormControl,FormGroup,ControlLabel,Checkbox,Button } from "react-bootstrap";
import * as API from "../services/api";

export interface SubscribeProps {  
  presentationId: number;
}
export interface SubscribeState {
  subscription:API.Subscription,
  presentation:API.Presentation;
}

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

      }
     
    };
    API.defaultHeaders["Authorization"] = "Bearer " + localStorage.token;
    this.handleChanged = this.handleChanged.bind(this);
  }
  userAPI = new API.UsercontrollerApi();
  subscriptionAPI = new API.SubscriptioncontrollerApi();
  handleChanged(e:any) {
    this.setState({
      subscription: {
        [e.target.name]: e.target.value
      }
    });
  }
  componentDidMount() {
    let presentationAPI = new API.PresentationcontrollerApi();
    presentationAPI.getSingleUsingGET({"id":this.props.presentationId})
    .then((response: API.Presentation) => {
      this.setState({
        presentation: response
      });
    }).catch((err) => {
      console.log(err);
    });
  }
  submit(){
   let presentationId = this.state.presentation.presentationId || -1; 
   this.subscriptionAPI.addSubscriptionUsingPOST(
     {presentationId, subscription: this.state.subscription})
     .catch((err) => {
     console.log("Submitting Subscription failed with error: "+err);
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
          <FormControl componentClass="select" name="sandwich_type" onChange={this.handleChanged}
            placeholder="Sandwichtyp" >
            <option value="vegi">vegi</option>
            <option value="fleisch">fleisch</option>
          </FormControl>
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

import * as React from "react";
import { FormControl, FormGroup, ControlLabel, Checkbox, Button } from "react-bootstrap";
import update = require('react-addons-update');
import Select = require('react-select');
import { translate } from 'react-i18next';

import * as API from "../services/apiWrapper";

export interface SubscribeProps {
  presentationId: number,
  t: any
}
export interface SubscribeState {
  subscription: API.Subscription,
  presentation: API.Presentation;
  files: API.PresentationFileMeta[];
}

@translate(['subscribe', 'common'], { wait: true })
export class Subscribe extends React.Component<SubscribeProps, SubscribeState> {
  constructor(props: SubscribeProps) {
    super(props);
    this.state = {
      subscription: {
        presentationId: 0,
        sandwichType: "v",
        drink: "",
        user: undefined
      },
      presentation: {
        abstract: "",
        dateTime: new Date(),
        deadline: new Date(),
        location: "",
        presentationId: 0,
        speakerId: 0,
        title: "",
      },
      files: []
    };

    let presentationAPI = new API.PresentationcontrollerApi();
    presentationAPI.getSingleUsingGET({ "id": this.props.presentationId })
      .then((response: API.Presentation) => {
        this.setState({
          subscription: update(this.state.subscription, {
            presentationId: { $set: response.presentationId }
          }),
          presentation: response
        });
      }).catch((err) => {
        console.log(err);
      });
    new API.PresentationfilecontrollerApi().getFilesUsingGET({ presentationId: this.props.presentationId })
      .then(r => {
        this.setState({ files: r });
      })

    this.handleChanged = this.handleChanged.bind(this);
    this.handleSelectChanged = this.handleSelectChanged.bind(this);
    this.handleCheckBoxChanged = this.handleCheckBoxChanged.bind(this);
    this.submit = this.submit.bind(this);
  }
  userAPI = new API.UsercontrollerApi();
  subscriptionAPI = new API.SubscriptioncontrollerApi();
  t = this.props.t;
  sandwichTypes = [
    { value: "f", label: this.t("meat") },
    { value: "v", label: this.t("vegetarian") }
  ];
  handleChanged(e: any) {
    this.setState({
      subscription: update(this.state.subscription, {
        [e.target.name]: { $set: e.target.value }
      }),
      presentation: this.state.presentation
    });
  }
  handleCheckBoxChanged(e: any) {
    if (e.target.value) {
      this.setState({
        subscription: update(this.state.subscription, {
          drink: { $set: "1" }
        }),
      });
    } else {
      this.setState({
        subscription: update(this.state.subscription, {
          drink: { $set: "" }
        }),
      });
    }
  }
  handleSelectChanged(e: any) {
    this.setState({
      subscription: update(this.state.subscription, {
        sandwichType: { $set: e.value }
      }),
    });
  }
  componentDidMount() {

  }
  submit(e: any) {
    e.preventDefault();
    let presentationId = this.state.presentation.presentationId || -1;
    let params = {
      "presentationId": presentationId,
      "subscription": this.state.subscription
    };
    console.log(JSON.stringify(params["subscription"] || {}));


    this.subscriptionAPI.addSubscriptionUsingPOST(params)
      .then((result) => {
        console.log("Result: " + result);
      })
      .catch((err) => {
        console.log("Submitting Subscription failed with error: ");
        console.log(err);
      });
  }
  handleFileClick (file: API.PresentationFileMeta){
    
     return new API.UsercontrollerApi().getTemporaryTokenUsingGET()
      .then(tt=>{
         const url = `${API.backendPath}/presentation/${this.props.presentationId}/file/${file.presentationFileId}` + 
          `?tempToken=${tt}`;
         window.open(url, '_blank'); // Not optimal, but working
         return true;
      });
  }
  getFilesDisplay() {
      return        (
            this.state.files.map(fl => 
              fl.contentLink ? 
              (
                <a href={fl.contentLink} target="_blank" > {fl.displayName || ("File " + fl.presentationId!.toString())}
                </a> 
              )
              : (
            <a onClick={ ()=> this.handleFileClick(fl)} > {fl.displayName || ("File " + fl.presentationId!.toString())}
            </a> ) 
            )
        
        );
 
  }
  render() {
    let t = this.props.t;
    return (
      <form>
        <FormGroup controlId="formControlsPresentation">
          <ControlLabel>{t('common:presentation')}</ControlLabel>
          <br />
          <h2>{this.state.presentation.title}</h2>
          <p>
            {this.state.presentation.abstract}
          </p>
          <FormControl type="hidden" name="presentationID" value={this.state.presentation.presentationId} />
        </FormGroup>
        <FormGroup controlId="formControlsSandwichType">
          <ControlLabel>{t('sandwichType')}</ControlLabel>
          <br />
          <Select name="fileType"
            value={this.state.subscription.sandwichType}
            options={this.sandwichTypes}
            onChange={this.handleSelectChanged}
            clearable={false}
          />
        </FormGroup>
        <FormGroup controlId="formControlsDrink">


          <Checkbox name="drink" onChange={this.handleCheckBoxChanged}>
            {t('drink')}
          </Checkbox>
        </FormGroup>
        {this.getFilesDisplay()}
        <Button type="submit" bsStyle="primary" onClick={this.submit}>
          {t('subscribe')}
        </Button>
      </form>
    );
  }
}

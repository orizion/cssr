import * as React from "react";
import Select = require('react-select');
import update = require('react-addons-update');
import * as API from "../services/api";
import {translate } from "react-i18next";

import { FormControl,FormGroup,ControlLabel,Checkbox,Button, Row, Col, } from "react-bootstrap";

export interface PresentationFileUploadProps {
  t: any;

}
export interface PresentationFileUploadState {
  file: API.PresentationFileMeta;
}

@translate(['presentationFileUpload', 'common'], { wait: true })
export class PresentationFileUpload extends React.Component<PresentationFileUploadProps, PresentationFileUploadState> {
  constructor(props: PresentationFileUploadProps){
    super(props);
    this.state = {
      file: {}
    };
    API.defaultHeaders["Authorization"] = "Bearer " + localStorage.token;
  }
  handleChanged(e:any) {
    this.setState({[e.target.name]: e.target.value});
  }
  handleSelectChanged(e: any) {
    this.setState({
        file: update(this.state.file, {
            file : { $set: e.value }
        })
    });
  }
  render() {
    let t = this.props.t;
    return (
        <Col>
            <FormGroup controlId="formControlsPresentationFile">
            <ControlLabel>{t('presentationFile')}</ControlLabel>
            <br/>
            <FormControl type="file" name="file[]" key={0}
                onChange={this.handleChanged} />
            </FormGroup>
        </Col>
    );
  }
}

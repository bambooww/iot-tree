
module oc.hmi
{
    /**
     * for hmi edit and display
     */
    export class HMIPage
    {
        tarEle: HTMLElement;

        panels:HMIPanel[]=[];

        public constructor(target: string, opts: {})
		{
			if (!opts)
				opts = {};

			this.tarEle = <HTMLCanvasElement>document.getElementById(target);
			
			this.tarEle["hmi_page"] = this;
        }
        
        public addPanel(hmip:HMIPanel)
        {
            this.panels.push(hmip) ;
            this.tarEle.appendChild(hmip.getHTMLElement());
        }

        
    }
}
import {InsuranceType} from './insurance-type.model';

export interface Policy {
  id: number;
  type: InsuranceType;
  startDate: string;
  endDate: string;
  pricePaid: number;
}

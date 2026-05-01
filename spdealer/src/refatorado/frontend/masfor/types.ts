export interface Masfor {
  id?: number;
  tipo_for: string; // numeric in DB (decimal), but treated as string in forms
  descr_for: string;
  filler?: string;
  id_fil?: number;
}
